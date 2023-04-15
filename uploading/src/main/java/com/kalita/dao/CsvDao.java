package com.kalita.dao;

import com.kalita.utils.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.qos.logback.core.CoreConstants.COMMA_CHAR;

@Slf4j
@Component
public class CsvDao {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CsvDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static final String SQL_INSERT_PROFILE = "insert into profiles " +
            "(id, segment_id, proceed_date_time, version_id, idfa, hash_phone, hash_email) " +
            "values (nextval('profile_id_generator'), :SEGMENT_ID, :PROCEED_DATE_TIME, :VERSION_ID, :IDFA, :HASH_PHONE, :HASH_EMAIL)";
    private static final String SQL_INSERT_SEGMENT = "insert into segment " +
            "(id, segment_name, actual_version_id) " +
            "values (nextval('segment_id_generator'), :SEGMENT_NAME, :VERSION_ID)";

    public Long saveSegment(String name, UUID versionId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("SEGMENT_NAME", name)
                .addValue("VERSION_ID", versionId);
        namedParameterJdbcTemplate.update(SQL_INSERT_SEGMENT, parameterSource, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        Objects.requireNonNull(keys);
        return (Long) keys.get("id");
    }

    public void saveProfiles(Long segmentId, UUID versionId, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Long savedProfiles = saveProfilesByBatches(segmentId, versionId, reader);
            log.info("Save {} profiles", savedProfiles);
        } catch (IOException | DataAccessException e) {
            log.error("Something went wrong", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Long saveProfilesByBatches(Long segmentId,
                                       UUID versionId,
                                       BufferedReader reader
    ) {
        String[] headers = {"IDFA", "HASH_PHONE", "HASH_EMAIL"};
        LocalDateTime insertDate = LocalDateTime.now();
        AtomicInteger commonCount = new AtomicInteger();
        List<Map<String, Object>> batchList = reader.lines()
                .skip(1) // пропускаем заголовок
                .map(csvRow -> csvRow.split(String.valueOf(COMMA_CHAR), -1))
                .map(values -> IntStream.range(0, headers.length)
                        .boxed()
                        .collect(Collectors.toMap(i -> headers[i], i -> (Object) values[i])))
                .collect(StreamUtils.batchCollector(batchSize, batchValuesList -> {
                    batchValuesList.forEach(batchValues -> {
                        commonCount.getAndIncrement();
                        batchValues.put("SEGMENT_ID", segmentId);
                        batchValues.put("PROCEED_DATE_TIME", insertDate);
                        batchValues.put("VERSION_ID", versionId);
                    });
                    if (commonCount.get() > 100000) {
                        throw new RuntimeException();
                    }
                    namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_PROFILE, batchValuesList.toArray(Map[]::new));//записываем батч в базу
                }));
        log.info("{} batches remain to process", batchList.size());
        return commonCount.longValue();
    }
}

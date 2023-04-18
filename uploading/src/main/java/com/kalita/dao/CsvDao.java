package com.kalita.dao;

import com.kalita.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.qos.logback.core.CoreConstants.COMMA_CHAR;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvDao {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    private static final int BATCH_SIZE = 10000;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT_PROFILE = "insert into profiles " +
            "(id, segment_id, proceed_date_time, version_id, idfa, hash_phone, hash_email) " +
            "values (nextval('profile_id_generator'), :SEGMENT_ID, :PROCEED_DATE_TIME, :VERSION_ID, :IDFA, :HASH_PHONE, :HASH_EMAIL)";
    private static final String SQL_INSERT_SEGMENT = "insert into segment " +
            "(id, segment_name, actual_version_id) " +
            "values (nextval('segment_id_generator'), :SEGMENT_NAME, :VERSION_ID)";
    private final static List<String> HEADERS = List.of("idfa", "hash_phone", "hash_email");
    private static final String SELECT_PROFILES = "SELECT idfa, hash_phone, hash_email FROM profiles " +
            "WHERE segment_id = ? AND version_id = ?";
    private static final String GET_VERSION_BY_ID = "SELECT actual_version_id FROM segment WHERE id = ?";

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

    public void streamProfilesFromDb(OutputStream outputStream, Long segmentId, UUID versionId) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROFILES)) {
                preparedStatement.setLong(1, segmentId);
                preparedStatement.setObject(2, versionId);
                preparedStatement.setFetchSize(BATCH_SIZE);

                writeRowToOutputStream(outputStream, HEADERS);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    List<String> columns = new ArrayList<>();
                    for (String profileHeader : HEADERS) {
                        columns.add(resultSet.getString(profileHeader));
                    }
                    writeRowToOutputStream(outputStream, columns);
                    outputStream.flush();
                }
            }
        } catch (SQLException | IOException exception) {
            log.error("error while fetch file with profiles", exception);
        }
    }

    public UUID getActualVersionId(Long segmentId) {
        String versionId = jdbcTemplate.queryForObject(GET_VERSION_BY_ID,
                (rs, rowNum) -> rs.getString("actual_version_id"),
                segmentId);
        Objects.requireNonNull(versionId);
        return UUID.fromString(versionId);
    }

    private void writeRowToOutputStream(OutputStream outputStream, List<String> columns) throws IOException {
        String delimiter = String.valueOf(COMMA_CHAR);
        String row = String.join(delimiter, columns).concat("\n");
        outputStream.write(row.getBytes(StandardCharsets.UTF_8));
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
                    namedParameterJdbcTemplate.batchUpdate(SQL_INSERT_PROFILE, batchValuesList.toArray(Map[]::new));//записываем батч в базу
                }));
        log.info("{} batches remain to process", batchList.size());
        return commonCount.longValue();
    }


}

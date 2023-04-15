package com.kalita.service;

import com.kalita.entities.Profiles;
import com.kalita.entities.Segment;
import com.kalita.repositories.ProfileRepo;
import com.kalita.repositories.SegmentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ch.qos.logback.core.CoreConstants.COMMA_CHAR;

@Slf4j
@Service
public class SegmentServiceJpa implements SegmentService {

    private final SegmentRepo segmentRepo;
    private final ProfileRepo profileRepo;

    @Autowired
    public SegmentServiceJpa(SegmentRepo segmentRepo,
                             ProfileRepo profileRepo
    ) {
        this.segmentRepo = segmentRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    @Transactional
    public void saveSegment(String name, MultipartFile file) {
        Segment savedSegment = getSavedSegment(name);
        List<Profiles> profiles = parseCsv(file, savedSegment);
        profileRepo.saveAll(profiles);
    }

    public Segment getSavedSegment(String name) {
        Segment segment = new Segment();
        UUID versionId = UUID.randomUUID();
        segment.setSegmentName(name);
        segment.setActualVersionId(versionId);
        return segmentRepo.save(segment);
    }

    private List<Profiles> parseCsv(MultipartFile file, Segment segment) {
        List<Profiles> profiles = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()){
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .skip(1)
                    .forEach(profileRow -> {
                        Profiles profile = getProfile(segment, profileRow);
                        profiles.add(profile);
                    });
        } catch (IOException e) {
            log.error("Something went wrong", e);
        }
        return profiles;
    }

    private static Profiles getProfile(Segment segment, String profileRow) {
        String[] split = profileRow.split(String.valueOf(COMMA_CHAR));
        return Profiles.builder()
                .segment(segment)
                .proceedDateTime(LocalDateTime.now())
                .versionId(segment.getActualVersionId())
                .hashEmail(split[0])
                .hashPhone(split[1])
                .idfa(split[2])
                .build();
    }
}

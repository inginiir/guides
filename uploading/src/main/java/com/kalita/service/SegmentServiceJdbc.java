package com.kalita.service;

import com.kalita.dao.CsvDao;
import com.kalita.entities.Segment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Primary
public class SegmentServiceJdbc implements SegmentService {

    private final CsvDao csvDao;

    @Autowired
    public SegmentServiceJdbc(CsvDao csvDao) {
        this.csvDao = csvDao;
    }

    @Override
    @Transactional
    public void saveSegment(String name, MultipartFile file) {
        UUID versionId = UUID.randomUUID();
        Long segmentId = csvDao.saveSegment(name, versionId);
        csvDao.saveProfiles(segmentId, versionId, file);
    }
}

package com.kalita.service;

import com.kalita.dao.CsvDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.UUID;

@Service
@Primary
public class SegmentServiceJdbc implements SegmentService, StreamService {

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

    @Override
    @Transactional(readOnly = true)
    public void streamProfiles(OutputStream outputStream, Long segmentId) {
        UUID versionId = csvDao.getActualVersionId(segmentId);
        csvDao.streamProfilesFromDb(outputStream, segmentId, versionId);
    }
}

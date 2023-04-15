package com.kalita.service;

import org.springframework.web.multipart.MultipartFile;

public interface SegmentService {

    void saveSegment(String name, MultipartFile file);
}

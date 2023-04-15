package com.kalita.controllers;

import com.kalita.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    private final SegmentService fileServiceJpa;
    private final SegmentService fileServiceJdbc;

    @Autowired
    public FileController(@Qualifier("segmentServiceJpa") SegmentService fileServiceJpa,
                          @Qualifier("segmentServiceJdbc") SegmentService fileServiceJdbc) {
        this.fileServiceJpa = fileServiceJpa;
        this.fileServiceJdbc = fileServiceJdbc;
    }

    @PostMapping("/jpa")
    public ResponseEntity<String> uploadFileJpa(
            @RequestParam("name") String name,
            @RequestPart("file") MultipartFile file
    ) {
        fileServiceJpa.saveSegment(name, file);
        return ResponseEntity.accepted().body("File accepted");
    }

    @PostMapping("/jdbc")
    public ResponseEntity<String> uploadFileJdbc(
            @RequestParam("name") String name,
            @RequestPart("file") MultipartFile file
    ) {
        fileServiceJdbc.saveSegment(name, file);
        return ResponseEntity.accepted().body("File accepted");
    }
}

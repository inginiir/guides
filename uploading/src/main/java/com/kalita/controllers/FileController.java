package com.kalita.controllers;

import com.kalita.service.FileService;
import com.kalita.service.SegmentService;
import com.kalita.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/file")
public class FileController {

    private final SegmentService fileServiceJpa;
    private final SegmentService fileServiceJdbc;
    private final FileService fileService;
    private final StreamService streamService;

    @Autowired
    public FileController(@Qualifier("segmentServiceJpa") SegmentService fileServiceJpa,
                          @Qualifier("segmentServiceJdbc") SegmentService fileServiceJdbc,
                          FileService fileService,
                          StreamService streamService) {
        this.fileServiceJpa = fileServiceJpa;
        this.fileServiceJdbc = fileServiceJdbc;
        this.fileService = fileService;
        this.streamService = streamService;
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

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFileWhole(@PathVariable("id") Long segmentId) throws IOException {
        File file = fileService.getWholeFile(segmentId);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = formHeaders(segmentId);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/paging/{id}")
    public ResponseEntity<Resource> getFileParts(@PathVariable("id") Long segmentId) throws IOException {
        File file = fileService.getPartsFile(segmentId);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = formHeaders(segmentId);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping(value = "/stream/{id}")
    public ResponseEntity<StreamingResponseBody> streamSegment(@PathVariable("id") Long segmentId) {
        StreamingResponseBody responseBody = out -> {
            streamService.streamProfiles(out, segmentId);
            out.flush();
            out.close();
        };
        HttpHeaders headers = formHeaders(segmentId);
        return ResponseEntity.ok().headers(headers).body(responseBody);
    }

    private HttpHeaders formHeaders(Long segmentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION, String.format("attachment; filename=segment_%d.csv", segmentId));
        return headers;
    }
}

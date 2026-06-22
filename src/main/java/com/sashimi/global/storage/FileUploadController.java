package com.sashimi.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/instructor/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStoragePort fileStoragePort;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("image") MultipartFile image) {
        String url = fileStoragePort.store(image);
        return ResponseEntity.ok(FileUploadResponse.of(url));
    }

    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileKeyResponse> uploadVideo(@RequestParam("video") MultipartFile video) {
        String key = fileStoragePort.storeVideo(video);
        return ResponseEntity.ok(FileKeyResponse.of(key));
    }

    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileKeyResponse> uploadAttachment(@RequestParam("file") MultipartFile file) {
        String key = fileStoragePort.storeAttachment(file);
        return ResponseEntity.ok(FileKeyResponse.of(key));
    }
}
package com.sashimi.global.storage;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/instructor/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStoragePort fileStoragePort;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }

        byte[] bytes;
        try {
            bytes = image.getBytes();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        // 비공개 버킷에 저장하고, 만료 없는 공개 이미지 프록시 URL로 반환
        String key = fileStoragePort.storePrivate(bytes, image.getOriginalFilename(), "images");
        String url = "/files/images?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
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

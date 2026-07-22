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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@RestController
@RequestMapping("/instructor/files")
@RequiredArgsConstructor
public class FileUploadController {

    /** 허용 확장자 (악성 파일 업로드 차단 - OWASP A05 Injection) */
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of("mp4", "webm", "mov");
    private static final Set<String> ALLOWED_ATTACHMENT_EXTENSIONS = Set.of("pdf", "docx", "pptx", "xlsx", "zip");

    private final FileStoragePort fileStoragePort;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("image") MultipartFile image) {
        validateExtension(image, ALLOWED_IMAGE_EXTENSIONS);

        // 비공개 버킷에 저장하고, 만료 없는 공개 이미지 프록시 URL로 반환
        // (파일 전체를 힙에 버퍼링하지 않고 스트리밍으로 업로드)
        String key = fileStoragePort.storePrivateStream(image, "images");
        String url = "/files/images?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
        return ResponseEntity.ok(FileUploadResponse.of(url));
    }

    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileKeyResponse> uploadVideo(@RequestParam("video") MultipartFile video) {
        validateExtension(video, ALLOWED_VIDEO_EXTENSIONS);
        String key = fileStoragePort.storeVideo(video);
        return ResponseEntity.ok(FileKeyResponse.of(key));
    }

    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileKeyResponse> uploadAttachment(@RequestParam("file") MultipartFile file) {
        validateExtension(file, ALLOWED_ATTACHMENT_EXTENSIONS);
        String key = fileStoragePort.storeAttachment(file);
        return ResponseEntity.ok(FileKeyResponse.of(key));
    }

    /** 파일이 비어있지 않은지 + 허용 확장자인지 검증 */
    private void validateExtension(MultipartFile file, Set<String> allowedExtensions) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }
    }
}

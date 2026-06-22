package com.sashimi.global.storage;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Profile("local")
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            file.transferTo(uploadPath.resolve(fileName));

            return baseUrl + "/uploads/" + fileName;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String storePrivate(byte[] bytes, String originalFilename, String folder) {
        throw new UnsupportedOperationException("로컬 환경에서는 private 파일 저장을 지원하지 않습니다.");
    }

    @Override
    public String generatePresignedDownloadUrl(String s3Key, int expiryMinutes) {
        throw new UnsupportedOperationException("로컬 환경에서는 presigned URL을 지원하지 않습니다.");
    }

    @Override
    public String storeVideo(MultipartFile file) {
        throw new UnsupportedOperationException("로컬 환경에서는 영상 업로드를 지원하지 않습니다.");
    }

    @Override
    public String storeAttachment(MultipartFile file) {
        throw new UnsupportedOperationException("로컬 환경에서는 자료 업로드를 지원하지 않습니다.");
    }

    @Override
    public void archiveFile(String s3Key) {
        // 로컬 환경은 콜드 스토리지가 없어 동작하지 않음 (no-op)
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
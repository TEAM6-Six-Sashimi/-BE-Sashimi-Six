package com.sashimi.global.storage;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Primary
@Profile("s3")
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties properties;

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }

        String key = "images/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getS3().getBucketImages())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (Exception e) {
            log.error("[S3] 이미지 업로드 실패 - key: {}, cause: {}", key, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return "https://" + properties.getCloudFront().getImagesDomain() + "/" + key;
    }

    @Override
    public String storePrivate(byte[] bytes, String originalFilename, String folder) {
        String key = folder + "/" + UUID.randomUUID() + getExtension(originalFilename);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getS3().getBucketDocs())
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (Exception e) {
            log.error("[S3] private 파일 업로드 실패 - bucket: {}, key: {}, cause: {}",
                    properties.getS3().getBucketDocs(), key, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return key;
    }

    @Override
    public String storeVideo(MultipartFile file) {
        return storeToPrivateBucket(file, properties.getS3().getBucketVideos(), "videos/lectures");
    }

    @Override
    public String storeAttachment(MultipartFile file) {
        return storeToPrivateBucket(file, properties.getS3().getBucketAttachments(), "materials/lectures");
    }

    private String storeToPrivateBucket(MultipartFile file, String bucket, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String key = folder + "/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        return key;
    }

    @Override
    public String generatePresignedDownloadUrl(String s3Key, int expiryMinutes) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiryMinutes))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(properties.getS3().getBucketDocs())
                        .key(s3Key)
                        .build())
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public byte[] downloadPrivate(String s3Key) {
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(properties.getS3().getBucketDocs())
                            .key(s3Key)
                            .build()
            );
            return response.asByteArray();
        } catch (Exception e) {
            log.error("[S3] 파일 다운로드 실패 - key: {}, cause: {}", s3Key, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public void archiveFile(String s3Key) {
        try {
            s3Client.putObjectTagging(PutObjectTaggingRequest.builder()
                    .bucket(properties.getS3().getBucketVideos())
                    .key(s3Key)
                    .tagging(Tagging.builder()
                            .tagSet(Tag.builder().key("archive").value("true").build())
                            .build())
                    .build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public String generateVideoUrl(String s3Key, int expiryMinutes) {
        return presign(properties.getS3().getBucketVideos(), s3Key, expiryMinutes);
    }

    @Override
    public String generateAttachmentUrl(String s3Key, int expiryMinutes) {
        return presign(properties.getS3().getBucketAttachments(), s3Key, expiryMinutes);
    }

    private String presign(String bucket, String s3Key, int expiryMinutes) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiryMinutes))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .build())
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public void deleteFromDocs(String s3Key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getS3().getBucketDocs())
                    .key(s3Key)
                    .build());
        } catch (Exception e) {
            log.error("[S3] 파일 삭제 실패 - key: {}, cause: {}", s3Key, e.getMessage(), e);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}

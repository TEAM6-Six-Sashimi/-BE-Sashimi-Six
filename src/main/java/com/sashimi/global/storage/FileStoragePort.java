package com.sashimi.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String store(MultipartFile file);
    String storePrivate(byte[] bytes, String originalFilename, String folder);
    String generatePresignedDownloadUrl(String s3Key, int expiryMinutes);

    /** 강의 영상 업로드 (비공개, sashimi-videos) → S3 key 반환 */
    String storeVideo(MultipartFile file);

    /** 강의 자료 업로드 (비공개, sashimi-attachments) → S3 key 반환 */
    String storeAttachment(MultipartFile file);

    /** 영상 객체에 아카이브 태그(archive=true) 부착 → 라이프사이클이 콜드 스토리지로 이동 */
    void archiveFile(String s3Key);
    byte[] downloadPrivate(String s3Key);

    /** 영상 시청용 presigned URL (sashimi-videos) */
    String generateVideoUrl(String s3Key, int expiryMinutes);

    /** 강의 자료 다운로드용 presigned URL (sashimi-attachments) */
    String generateAttachmentUrl(String s3Key, int expiryMinutes);
}
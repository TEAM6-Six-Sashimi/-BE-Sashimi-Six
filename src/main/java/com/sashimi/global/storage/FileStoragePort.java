package com.sashimi.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String store(MultipartFile file);
    String storePrivate(byte[] bytes, String originalFilename, String folder);

    /** storePrivate와 동일하지만 파일 전체를 메모리에 버퍼링하지 않고 스트리밍으로 업로드 */
    String storePrivateStream(MultipartFile file, String folder);

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

    /** sashimi-docs 버킷에서 객체 삭제 (업로드 실패 시 보상 트랜잭션용) */
    void deleteFromDocs(String s3Key);
}
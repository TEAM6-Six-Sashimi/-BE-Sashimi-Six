package com.sashimi.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String store(MultipartFile file);
    String storePrivate(byte[] bytes, String originalFilename, String folder);
    String generatePresignedDownloadUrl(String s3Key, int expiryMinutes);
    byte[] downloadPrivate(String s3Key);
}
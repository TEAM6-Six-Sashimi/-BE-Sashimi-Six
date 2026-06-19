package com.sashimi.member.infrastructure;

import com.sashimi.member.application.port.FileStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public String upload(byte[] fileBytes, String fileName) {
        try {
            String ext = "";
            if (fileName != null && fileName.contains(".")) {
                ext = fileName.substring(fileName.lastIndexOf("."));
            }
            String savedName = UUID.randomUUID() + ext;

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(savedName);
            Files.write(filePath, fileBytes);

            return baseUrl + "/" + uploadDir + savedName;
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", fileName, e);
            throw new RuntimeException("파일 저장에 실패했습니다.");
        }
    }
}

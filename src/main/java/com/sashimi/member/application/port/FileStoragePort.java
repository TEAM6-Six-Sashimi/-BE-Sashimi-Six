package com.sashimi.member.application.port;

public interface FileStoragePort {
    String upload(byte[] fileBytes, String fileName);
}

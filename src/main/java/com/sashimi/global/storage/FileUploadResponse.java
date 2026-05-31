package com.sashimi.global.storage;

public record FileUploadResponse(String url) {
    public static FileUploadResponse of(String url) {
        return new FileUploadResponse(url);
    }
}
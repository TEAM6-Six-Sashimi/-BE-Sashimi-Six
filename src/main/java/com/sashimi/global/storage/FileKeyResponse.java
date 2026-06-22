package com.sashimi.global.storage;

public record FileKeyResponse(String key) {
    public static FileKeyResponse of(String key) {
        return new FileKeyResponse(key);
    }
}

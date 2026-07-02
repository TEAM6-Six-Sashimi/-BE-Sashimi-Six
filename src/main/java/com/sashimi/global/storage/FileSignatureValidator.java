package com.sashimi.global.storage;

import java.util.Arrays;

public final class FileSignatureValidator {

    private FileSignatureValidator() {
    }

    public static boolean isJpegPngOrPdf(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return false;
        }
        byte[] header = Arrays.copyOf(bytes, 4);
        // JPEG: FF D8 FF
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) return true;
        // PNG: 89 50 4E 47
        if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) return true;
        // PDF: %PDF (25 50 44 46)
        return header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46;
    }

    public static boolean isDocx(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return false;
        }
        byte[] header = Arrays.copyOf(bytes, 4);
        // DOCX는 ZIP 컨테이너: PK.. (50 4B 03 04)
        return header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04;
    }
}

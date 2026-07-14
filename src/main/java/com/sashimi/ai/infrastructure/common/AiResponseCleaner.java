package com.sashimi.ai.infrastructure.common;

public final class AiResponseCleaner {

    private AiResponseCleaner() {
    }

    public static String removeMarkdownFence(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String cleaned = text.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }

        return cleaned.trim();
    }
}
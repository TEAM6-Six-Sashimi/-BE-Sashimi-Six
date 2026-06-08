package com.sashimi.ai.infrastructure.gemini;

public final class GeminiResponseCleaner {

    private GeminiResponseCleaner() {
    }

    public static String removeMarkdownFence(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("```json", "")
                .replace("```JSON", "")
                .replace("```", "")
                .trim();
    }
}
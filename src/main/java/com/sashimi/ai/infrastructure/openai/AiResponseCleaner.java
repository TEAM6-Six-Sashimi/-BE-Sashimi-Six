package com.sashimi.ai.infrastructure.openai;

public final class AiResponseCleaner {

    private AiResponseCleaner() {
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
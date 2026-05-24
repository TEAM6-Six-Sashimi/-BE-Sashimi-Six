package com.sashimi.resume.domain.model;

public class Resume {

    private final Long resumeId;
    private final Long userId;
    private final String title;
    private final String content;

    public Resume(Long resumeId, Long userId, String title, String content) {
        this.resumeId = resumeId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Long resumeId() {
        return resumeId;
    }

    public Long userId() {
        return userId;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }
}

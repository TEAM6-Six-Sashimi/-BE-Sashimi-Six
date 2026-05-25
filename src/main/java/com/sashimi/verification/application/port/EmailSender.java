package com.sashimi.verification.application.port;

public interface EmailSender {

    void send(String to, String subject, String content);
}
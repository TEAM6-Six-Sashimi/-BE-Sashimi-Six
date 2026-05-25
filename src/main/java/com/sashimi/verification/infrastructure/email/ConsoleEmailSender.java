package com.sashimi.verification.infrastructure.email;

import com.sashimi.verification.application.port.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!mailgun")
@Component
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String content) {
        log.info("Email send requested. to={}, subject={}, content={}", to, subject, content);
    }
}
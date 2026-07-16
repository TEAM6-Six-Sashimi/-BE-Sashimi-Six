package com.sashimi.ncs.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class NcsStartupSyncRunner implements ApplicationRunner {

    private final NcsSyncService ncsSyncService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ncsSyncService.syncDefaultMappingsIfNeeded();
        } catch (Exception e) {
            log.warn("NCS 기본 매핑 자동 동기화 실패. 서버는 계속 실행합니다.", e);
        }
    }
}
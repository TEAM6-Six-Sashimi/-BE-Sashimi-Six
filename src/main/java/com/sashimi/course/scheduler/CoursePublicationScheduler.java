package com.sashimi.course.scheduler;

import com.sashimi.course.application.usecase.CourseCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoursePublicationScheduler {

    private final CourseCommandUseCase courseCommandUseCase;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void closeExpiredCourses() {
        log.info("[CoursePublication] 공개 기간 만료 강의 비공개 처리 시작");
        int closed = courseCommandUseCase.closeExpiredCourses();
        log.info("[CoursePublication] 공개 기간 만료 강의 비공개 처리 완료 - {}건", closed);
    }
}

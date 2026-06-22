package com.sashimi.course.scheduler;

import com.sashimi.course.application.usecase.CourseCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseArchiveScheduler {

    private final CourseCommandUseCase courseCommandUseCase;

    @Scheduled(cron = "0 0 2 * * *")
    public void archiveInactiveCourses() {
        log.info("[CourseArchive] 비공개+무수강 강의 영상 아카이브 시작");
        int archived = courseCommandUseCase.archiveInactiveCourses();
        log.info("[CourseArchive] 영상 아카이브 완료 - {}건", archived);
    }
}

package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 개발 초기 단계에서 사용하는 임시 이력서 저장소 Adapter.
 *
 * DB/JPA 연결 전까지 ResumeRepository Bean 역할을 한다.
 */
@Repository
public class InMemoryResumeRepositoryAdapter implements ResumeRepository {

    private final Map<Long, Resume> resumes = new HashMap<>();

    public InMemoryResumeRepositoryAdapter() {
        // Postman 테스트용 샘플 이력서
        Resume sampleResume = new Resume(
                1L,
                1L,
                "백엔드 개발자 이력서",
                "Java와 Spring Boot 기반 REST API 프로젝트 경험이 있습니다."
        );

        resumes.put(sampleResume.resumeId(), sampleResume);
    }

    @Override
    public Optional<Resume> findByIdAndUserId(Long resumeId, Long userId) {
        return Optional.ofNullable(resumes.get(resumeId))
                .filter(resume -> resume.userId().equals(userId));
    }
}

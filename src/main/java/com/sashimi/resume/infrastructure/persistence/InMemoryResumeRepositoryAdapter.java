package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 개발 초기 단계에서 사용하는 임시 이력서 저장소 Adapter.
 *
 * DB/JPA 연결 전까지 ResumeRepository Bean 역할을 한다.
 */
@Repository
public class InMemoryResumeRepositoryAdapter implements ResumeRepository {

    private final Map<Long, Resume> resumes = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(2);


    public InMemoryResumeRepositoryAdapter() {
        // Postman 테스트용 샘플 이력서
        Resume sampleResume = new Resume(
                1L,
                1L,
                "백엔드 개발자 이력서",
                """
                {
                  "basic": {
                    "name": "박학생",
                    "email": "student1@test.com",
                    "phone": "010-1234-5678",
                    "position": "Backend Developer"
                  },
                  "education": [
                    {
                      "school": "울지대학교",
                      "major": "의료IT학과",
                      "startYear": "2022",
                      "endYear": "2027"
                    }
                  ],
                  "career": [],
                  "skills": ["Java", "Spring Boot", "MySQL"],
                  "certificates": [],
                  "summary": "Spring Boot와 REST API 개발 경험이 있는 신입 백엔드 개발자입니다."
                }
                """
        );

        resumes.put(sampleResume.resumeId(), sampleResume);
    }

    @Override
    public Optional<Resume> findByIdAndUserId(Long resumeId, Long userId) {
        return Optional.ofNullable(resumes.get(resumeId))
                .filter(resume -> resume.userId().equals(userId));
    }

    @Override
    public Resume save(Resume resume) {
        Long resumeId = resume.resumeId() == null ? sequence.getAndIncrement() : resume.resumeId();
        Resume savedResume = resume.withId(resumeId);
        resumes.put(resumeId, savedResume);
        return savedResume;
    }

    @Override
    public List<Resume> findAllByUserId(Long userId) {
        return resumes.values().stream()
                .filter(resume -> resume.userId().equals(userId))
                .sorted(Comparator.comparing(Resume::createdAt).reversed())
                .toList();
    }

    @Override
    public void delete(Resume resume) {
        resumes.remove(resume.resumeId());
    }

}

package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryResumeRepositoryAdapter
        implements ResumeRepository {

    private final Map<Long, Resume> resumes =
            new HashMap<>();

    private final AtomicLong sequence =
            new AtomicLong(2);

    public InMemoryResumeRepositoryAdapter() {
        Resume sampleResume = Resume.create(
                1L,
                List.of(
                        new ResumeEducation(
                                "한국대학교",
                                YearMonth.of(2016, 3),
                                YearMonth.of(2020, 2),
                                EducationDegree.BACHELOR,
                                "컴퓨터공학과",
                                GraduationStatus.GRADUATED,
                                "데이터과학과 부전공"
                        )
                ),
                false,
                List.of(
                        new ResumeCareer(
                                "(주)테크스타트",
                                YearMonth.of(2022, 3),
                                null,
                                true,
                                EmploymentType.FULL_TIME,
                                null,
                                "백엔드 개발자 / 주임"
                        )
                ),
                true
        ).withId(1L);

        resumes.put(
                sampleResume.resumeId(),
                sampleResume
        );
    }

    @Override
    public Optional<Resume> findByIdAndUserId(
            Long resumeId,
            Long userId
    ) {
        return Optional.ofNullable(
                        resumes.get(resumeId)
                )
                .filter(resume ->
                        resume.userId().equals(userId)
                );
    }

    @Override
    public Resume save(Resume resume) {
        Long resumeId = resume.resumeId() == null
                ? sequence.getAndIncrement()
                : resume.resumeId();

        Resume savedResume =
                resume.withId(resumeId);

        resumes.put(
                resumeId,
                savedResume
        );

        return savedResume;
    }

    @Override
    public List<Resume> findAllByUserId(
            Long userId
    ) {
        return resumes.values().stream()
                .filter(resume ->
                        resume.userId().equals(userId)
                )
                .sorted(
                        Comparator.comparing(
                                Resume::createdAt
                        ).reversed()
                )
                .toList();
    }

    @Override
    public void delete(Resume resume) {
        resumes.remove(resume.resumeId());
    }
}
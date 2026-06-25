package com.sashimi.recommendation.application.service;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class JobPostingResumeSummaryBuilder {

    public String build(Resume resume) {
        if (resume == null) {
            return "";
        }

        return """
                [사용자 이력서 정보]
                학력:
                %s

                경력:
                %s

                자격증:
                %s
                """.formatted(
                buildEducations(resume.educations()),
                buildCareers(resume.careers(), resume.entryLevel()),
                buildCertifications(resume.certifications())
        );
    }

    private String buildEducations(List<ResumeEducation> educations) {
        if (educations == null || educations.isEmpty()) {
            return "- 등록된 학력 없음";
        }

        return educations.stream()
                .map(education -> "- %s / %s / %s / %s"
                        .formatted(
                                education.schoolName(),
                                education.major(),
                                education.degree(),
                                education.graduationStatus()
                        ))
                .toList()
                .toString();
    }

    private String buildCareers(
            List<ResumeCareer> careers,
            boolean entryLevel
    ) {
        if (entryLevel || careers == null || careers.isEmpty()) {
            return "- 신입 또는 등록된 경력 없음";
        }

        return careers.stream()
                .map(career -> "- %s / %s / %s / %s"
                        .formatted(
                                career.companyName(),
                                career.jobTitle(),
                                career.employmentType(),
                                formatPeriod(career)
                        ))
                .toList()
                .toString();
    }

    private String buildCertifications(List<ResumeCertification> certifications) {
        if (certifications == null || certifications.isEmpty()) {
            return "- 등록된 자격증 없음";
        }

        return certifications.stream()
                .map(certification -> "- %s / %s / %s / %s"
                        .formatted(
                                certification.name(),
                                certification.type(),
                                certification.issuer(),
                                certification.acquiredDate()
                        ))
                .toList()
                .toString();
    }

    private String formatPeriod(ResumeCareer career) {
        YearMonth end = career.currentlyEmployed()
                ? YearMonth.now()
                : career.endYearMonth();

        long months = ChronoUnit.MONTHS.between(
                career.startYearMonth(),
                end
        ) + 1;

        long years = months / 12;
        long remainingMonths = months % 12;

        return "%s ~ %s, 총 %d년 %d개월".formatted(
                career.startYearMonth(),
                career.currentlyEmployed() ? "재직중" : career.endYearMonth(),
                years,
                remainingMonths
        );
    }
}
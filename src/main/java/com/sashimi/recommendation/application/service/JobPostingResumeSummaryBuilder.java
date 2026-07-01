package com.sashimi.recommendation.application.service;

import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeCertification;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.springframework.stereotype.Component;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeCertificationType;

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
                .map(education -> "- 학교: %s, 전공: %s, 학위: %s, 졸업상태: %s"
                        .formatted(
                                education.schoolName(),
                                education.major(),
                                labelOf(education.degree()),
                                labelOf(education.graduationStatus())
                        ))
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private String buildCareers(
            List<ResumeCareer> careers,
            boolean entryLevel
    ) {
        if (entryLevel || careers == null || careers.isEmpty()) {
            return "- 신입 또는 등록된 경력 없음";
        }

        return careers.stream()
                .map(career -> "- 회사: %s, 직무/직책: %s, 재직형태: %s, 기간: %s"
                        .formatted(
                                career.companyName(),
                                career.jobTitle(),
                                labelOf(career.employmentType(), career.customEmploymentType()),
                                formatPeriod(career)
                        ))
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private String buildCertifications(List<ResumeCertification> certifications) {
        if (certifications == null || certifications.isEmpty()) {
            return "- 등록된 자격증 없음";
        }

        return certifications.stream()
                .map(certification -> "- 이름: %s, 유형: %s, 발급기관: %s, 취득일: %s"
                        .formatted(
                                certification.name(),
                                labelOf(certification.type()),
                                certification.issuer(),
                                certification.acquiredDate()
                        ))
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private String formatPeriod(ResumeCareer career) {
        YearMonth end = career.currentlyEmployed()
                ? YearMonth.now()
                : career.endYearMonth();

        if (end == null) {
            return "%s ~ 종료월 미입력".formatted(career.startYearMonth());
        }

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

    private String labelOf(EducationDegree degree) {
        return switch (degree) {
            case HIGH_SCHOOL -> "고졸";
            case ASSOCIATE -> "전문학사";
            case BACHELOR -> "학사";
            case MASTER -> "석사";
            case DOCTOR -> "박사";
        };
    }

    private String labelOf(GraduationStatus status) {
        return switch (status) {
            case GRADUATED -> "졸업";
            case EXPECTED_GRADUATION -> "졸업 예정";
            case ENROLLED -> "재학";
            case LEAVE_OF_ABSENCE -> "휴학";
            case DROPPED_OUT -> "중퇴";
        };
    }

    private String labelOf(
            EmploymentType employmentType,
            String customEmploymentType
    ) {
        if (employmentType == EmploymentType.OTHER) {
            return customEmploymentType == null || customEmploymentType.isBlank()
                    ? "기타"
                    : customEmploymentType;
        }

        return switch (employmentType) {
            case PART_TIME -> "아르바이트";
            case FULL_TIME -> "정규직";
            case CONTRACT -> "계약직";
            case FREELANCER -> "프리랜서";
            case OTHER -> "기타";
        };
    }

    private String labelOf(ResumeCertificationType type) {
        return switch (type) {
            case CERTIFICATE -> "자격증";
            case LANGUAGE -> "어학";
            case DRIVER_LICENSE -> "운전면허";
            case EDUCATION -> "교육이수";
            case OTHER -> "기타";
        };
    }
}
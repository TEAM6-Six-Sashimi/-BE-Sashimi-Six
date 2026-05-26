package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

// 해당 클래스는 AI 기능 도입 후 변경 예정 Stub → OpenAi
// 현재는 하드코딩 되어있음
@Component
@Profile("local")
public class StubJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(JobPostingRecommendation recommendation) {
        boolean resumeBased = recommendation.resumeBased();

        return new JobPostingRecommendationAnalyzeResult(
                "Frontend Developer",
                resumeBased ? 56 : null,
                List.of(
                        new RequiredSkillRecommendation("React", "Frontend", resumeBased ? true : null),
                        new RequiredSkillRecommendation("TypeScript", "Frontend", resumeBased ? true : null),
                        new RequiredSkillRecommendation("Next.js", "Frontend", resumeBased ? true : null),
                        new RequiredSkillRecommendation("GraphQL", "API", resumeBased ? false : null),
                        new RequiredSkillRecommendation("AWS", "Cloud", resumeBased ? false : null),
                        new RequiredSkillRecommendation("Docker", "DevOps", resumeBased ? false : null)
                ),
                List.of(
                        new CourseRecommendation(
                                1L,
                                "실무에서 바로 쓰는 GraphQL 완전 정복",
                                "김민준",
                                "GraphQL",
                                "공고의 GraphQL 필수 역량과 직접 관련된 강의입니다."
                        ),
                        new CourseRecommendation(
                                2L,
                                "Docker & Kubernetes 입문부터 실전까지",
                                "이서연",
                                "Docker",
                                "CI/CD와 컨테이너 기반 운영 역량 보완에 적합합니다."
                        ),
                        new CourseRecommendation(
                                3L,
                                "AWS 기초부터 배포까지",
                                "박지훈",
                                "AWS",
                                "클라우드 환경 배포 경험을 보완할 수 있습니다."
                        )
                ),
                List.of(
                        new CertificateRecommendation(
                                1L,
                                "AWS Cloud Practitioner",
                                "클라우드 기초 이해 및 AWS 서비스 활용 역량 보완에 적합합니다.",
                                List.of("AWS"),
                                "쉬움"
                        ),
                        new CertificateRecommendation(
                                2L,
                                "정보처리기사",
                                "소프트웨어 개발 전반의 역량 증명에 도움이 됩니다.",
                                List.of("CI/CD", "Git"),
                                "보통"
                        ),
                        new CertificateRecommendation(
                                3L,
                                "AWS Solutions Architect",
                                "AWS 인프라 설계 및 운영 역량 보완에 적합합니다.",
                                List.of("AWS", "Docker"),
                                "어려움"
                        )
                )
        );
    }
}

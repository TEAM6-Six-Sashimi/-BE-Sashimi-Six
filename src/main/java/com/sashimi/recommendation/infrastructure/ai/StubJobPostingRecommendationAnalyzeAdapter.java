package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local")
public class StubJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(
            JobPostingRecommendation recommendation,
            AiPrompt prompt
    ) {
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
                                "공고에서 GraphQL 역량을 요구하므로 보완 학습에 적합합니다."
                        ),
                        new CourseRecommendation(
                                2L,
                                "Docker & Kubernetes 입문부터 실전까지",
                                "이서준",
                                "Docker",
                                "컨테이너 기반 운영 역량을 보완하기 위한 강의입니다."
                        ),
                        new CourseRecommendation(
                                3L,
                                "AWS 기초부터 배포까지",
                                "박지훈",
                                "AWS",
                                "클라우드 배포 경험을 보완하는 데 도움이 됩니다."
                        )
                ),
                List.of(
                        new CertificateRecommendation(
                                1L,
                                "AWS Cloud Practitioner",
                                "클라우드 기초 이해와 AWS 서비스 활용 역량을 증명하는 데 적합합니다.",
                                List.of("AWS"),
                                "쉬움"
                        ),
                        new CertificateRecommendation(
                                2L,
                                "정보처리기사",
                                "소프트웨어 개발 전반의 기본 역량을 증명하는 데 도움이 됩니다.",
                                List.of("CI/CD", "Git"),
                                "보통"
                        ),
                        new CertificateRecommendation(
                                3L,
                                "AWS Solutions Architect",
                                "AWS 인프라 설계 및 운영 역량을 보완하는 데 적합합니다.",
                                List.of("AWS", "Docker"),
                                "어려움"
                        )
                )
        );
    }
}
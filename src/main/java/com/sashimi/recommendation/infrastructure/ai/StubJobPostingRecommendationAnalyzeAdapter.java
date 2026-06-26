package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.FitAnalysisCategory;
import com.sashimi.recommendation.domain.model.FitAnalysisItem;
import com.sashimi.recommendation.domain.model.FitStatus;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingSummary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local & !gemini")
public class StubJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(
            JobPostingRecommendation recommendation,
            AiPrompt prompt
    ) {
        JobPostingSummary summary = new JobPostingSummary(
                "프론트엔드 개발자",
                List.of(
                        "React/TypeScript/Next.js 실무 경험 3년 이상",
                        "REST API 연동 및 상태관리 경험",
                        "Git 기반 협업 경험"
                ),
                List.of(
                        "GraphQL 또는 Apollo Client 경험",
                        "AWS S3, CloudFront 등 클라우드 서비스 활용 경험",
                        "Docker 또는 CI/CD 파이프라인 구성 경험"
                ),
                "관련 경력 3년 이상",
                "사용자 중심의 프론트엔드 개발 및 유지보수, React 기반 SPA 설계 및 구현, 백엔드 팀과의 API 연동"
        );

        JobFitAnalysis fitAnalysis = new JobFitAnalysis(
                new FitAnalysisItem(
                        FitAnalysisCategory.EDUCATION,
                        FitStatus.SATISFIED,
                        "학사 이상",
                        "컴퓨터공학과 학사",
                        "학력은 공고에서 요구하는 조건을 충족합니다.",
                        List.of()
                ),
                new FitAnalysisItem(
                        FitAnalysisCategory.CAREER,
                        FitStatus.PARTIALLY_SATISFIED,
                        "관련 경력 3년 이상",
                        "프론트엔드 개발 1년 6개월",
                        "관련 경력이 있으나 요구 연차에는 다소 부족합니다.",
                        List.of("관련 경력 1년 6개월")
                ),
                new FitAnalysisItem(
                        FitAnalysisCategory.CERTIFICATION,
                        FitStatus.PARTIALLY_SATISFIED,
                        "정보처리기사, SQLD",
                        "정보처리기사",
                        "필수 자격증은 보유했지만 SQLD를 취득하면 적합도를 높일 수 있습니다.",
                        List.of("SQLD")
                ),
                List.of(
                        "학력 조건은 충족합니다.",
                        "경력은 일부 충족 상태이므로 프로젝트 경험을 보완하는 것이 좋습니다.",
                        "SQLD 자격증을 취득하면 데이터 활용 역량을 보완할 수 있습니다."
                )
        );

        return new JobPostingRecommendationAnalyzeResult(
                summary,
                fitAnalysis,
                List.of(
                        new CertificateRecommendation(
                                null,
                                "정보처리기사",
                                "소프트웨어 개발 전반의 기본 역량을 증명할 수 있는 자격증입니다.",
                                List.of("소프트웨어 개발", "데이터베이스", "네트워크"),
                                "보통"
                        ),
                        new CertificateRecommendation(
                                null,
                                "SQLD",
                                "데이터 조회와 모델링 역량을 보완하여 백엔드 및 프론트엔드 협업 이해도를 높일 수 있습니다.",
                                List.of("SQL", "데이터베이스"),
                                "보통"
                        )
                ),
                List.of()
        );
    }
}
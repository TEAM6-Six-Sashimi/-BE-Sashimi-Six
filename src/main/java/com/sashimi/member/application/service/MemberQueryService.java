package com.sashimi.member.application.service;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.member.application.usecase.MemberQueryUseCase;
import com.sashimi.member.domain.model.ApprovalStatus;
import com.sashimi.member.domain.model.InstructorApplication;
import com.sashimi.member.domain.model.InstructorCertification;
import com.sashimi.member.domain.repository.InstructorApplicationRepository;
import com.sashimi.member.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationDetailResponse;
import com.sashimi.member.presentation.api.response.MyInstructorApplicationListResponse;
import com.sashimi.member.presentation.api.response.RejectedApplicationListResponse;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService implements MemberQueryUseCase {

    private static final String FILE_DOWNLOAD_BASE = "/files/download?key=";

    private final InstructorApplicationRepository instructorApplicationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<InstructorApplicationListResponse> getPendingInstructorApplications() {
        return instructorApplicationRepository.findAllByStatus(ApprovalStatus.PENDING)
                .stream()
                .map(application -> {
                    User user = userRepository.findById(application.getUserId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    String categoryName = categoryRepository.findById(application.getCategoryId())
                            .map(Category::getName)
                            .orElse(null);
                    return InstructorApplicationListResponse.of(application, user, categoryName);
                })
                .toList();
    }

    @Override
    public InstructorApplicationDetailResponse getInstructorApplicationDetail(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        String profileImageUrl = toPresignedUrl(application.getProfileImagePath());
        String resumeFileUrl = toPresignedUrl(application.getResumeFilePath());
        List<String> certFileUrls = application.getCertifications() == null ? List.of() :
                application.getCertifications().stream()
                        .map(InstructorCertification::getFilePath)
                        .map(this::toPresignedUrl)
                        .toList();

        return InstructorApplicationDetailResponse.from(application, profileImageUrl, resumeFileUrl, certFileUrls);
    }

    @Override
    public List<MyInstructorApplicationListResponse> getMyInstructorApplications(Long userId) {
        return instructorApplicationRepository.findAllByUserId(userId)
                .stream()
                .map(MyInstructorApplicationListResponse::from)
                .toList();
    }

    @Override
    public MyInstructorApplicationDetailResponse getMyInstructorApplicationDetail(Long userId, Long applicationId) {
        InstructorApplication application = instructorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String profileImageUrl = toPresignedUrl(application.getProfileImagePath());
        String resumeFileUrl = toPresignedUrl(application.getResumeFilePath());
        List<String> certFileUrls = application.getCertifications() == null ? List.of() :
                application.getCertifications().stream()
                        .map(InstructorCertification::getFilePath)
                        .map(this::toPresignedUrl)
                        .toList();

        return MyInstructorApplicationDetailResponse.of(application, user, profileImageUrl, resumeFileUrl, certFileUrls);
    }

    private String toPresignedUrl(String s3Key) {
        if (s3Key == null) return null;
        return FILE_DOWNLOAD_BASE + s3Key;
    }

    @Override
    public List<RejectedApplicationListResponse> getRejectedInstructorApplications() {
        return instructorApplicationRepository.findAllByStatus(ApprovalStatus.REJECTED)
                .stream()
                .map(application -> {
                    User user = userRepository.findById(application.getUserId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    return RejectedApplicationListResponse.of(application, user);
                })
                .toList();
    }
}

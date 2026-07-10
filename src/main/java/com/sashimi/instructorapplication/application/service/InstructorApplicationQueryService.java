package com.sashimi.instructorapplication.application.service;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.instructorapplication.application.usecase.InstructorApplicationQueryUseCase;
import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.InstructorCertification;
import com.sashimi.instructorapplication.domain.repository.InstructorApplicationRepository;
import com.sashimi.instructorapplication.presentation.api.response.InstructorApplicationDetailResponse;
import com.sashimi.instructorapplication.presentation.api.response.InstructorApplicationListResponse;
import com.sashimi.instructorapplication.presentation.api.response.InstructorProfileResponse;
import com.sashimi.instructorapplication.presentation.api.response.MyInstructorApplicationDetailResponse;
import com.sashimi.instructorapplication.presentation.api.response.MyInstructorApplicationListResponse;
import com.sashimi.instructorapplication.presentation.api.response.RejectedApplicationListResponse;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorApplicationQueryService implements InstructorApplicationQueryUseCase {

    private static final int FILE_URL_EXPIRY_MINUTES = 30;

    private final InstructorApplicationRepository instructorApplicationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStoragePort fileStoragePort;

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

        String profileImageUrl = toDownloadUrl(application.getProfileImagePath());
        String resumeFileUrl = toDownloadUrl(application.getResumeFilePath());
        List<String> certFileUrls = application.getCertifications() == null ? List.of() :
                application.getCertifications().stream()
                        .map(InstructorCertification::getFilePath)
                        .map(this::toDownloadUrl)
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

        String profileImageUrl = toDownloadUrl(application.getProfileImagePath());
        String resumeFileUrl = toDownloadUrl(application.getResumeFilePath());
        List<String> certFileUrls = application.getCertifications() == null ? List.of() :
                application.getCertifications().stream()
                        .map(InstructorCertification::getFilePath)
                        .map(this::toDownloadUrl)
                        .toList();

        return MyInstructorApplicationDetailResponse.of(application, user, profileImageUrl, resumeFileUrl, certFileUrls);
    }

    @Override
    public InstructorProfileResponse getMyInstructorProfile(Long userId) {
        InstructorApplication application = instructorApplicationRepository.findApprovedByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String profileImageUrl = toDownloadUrl(application.getProfileImagePath());
        return InstructorProfileResponse.of(application, user, profileImageUrl);
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

    private String toDownloadUrl(String s3Key) {
        if (s3Key == null) return null;
        return fileStoragePort.generatePresignedDownloadUrl(s3Key, FILE_URL_EXPIRY_MINUTES);
    }
}

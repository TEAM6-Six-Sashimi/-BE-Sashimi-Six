package com.sashimi.instructorapplication.application.service;

import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.instructorapplication.application.command.ApplyInstructorCommand;
import com.sashimi.instructorapplication.application.event.InstructorApprovedEvent;
import com.sashimi.instructorapplication.application.port.DocxPort;
import com.sashimi.instructorapplication.application.usecase.InstructorApplicationCommandUseCase;
import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.InstructorCertification;
import com.sashimi.instructorapplication.domain.model.RejectionCategory;
import com.sashimi.instructorapplication.domain.repository.InstructorApplicationRepository;
import com.sashimi.certificate.application.port.OcrPort;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorApplicationCommandService implements InstructorApplicationCommandUseCase {

    private final InstructorApplicationRepository instructorApplicationRepository;
    private final UserRepository userRepository;
    private final OcrPort ocrPort;
    private final DocxPort docxPort;
    private final FileStoragePort fileStoragePort;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;

    @Override
    public void applyInstructor(ApplyInstructorCommand command) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            if (command.bio() == null || command.bio().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            if (command.motivationLetter() == null || command.motivationLetter().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            if (command.portfolioUrl() == null || command.portfolioUrl().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            if (!categoryRepository.findById(command.categoryId()).map(c -> true).orElse(false)) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            // 중복 신청 방지 - S3 업로드 전에 체크해야 orphan 파일 방지
            boolean alreadyApplied = instructorApplicationRepository
                    .existsByUserIdAndApprovalStatus(command.userId(), ApprovalStatus.PENDING);
            if (alreadyApplied) {
                throw new BusinessException(ErrorCode.ALREADY_APPLIED);
            }

            // 자격증 OCR 검증 (S3 업로드 전, 메모리에만 보관)
            record CertCandidate(OcrPort.OcrResult ocr, ApplyInstructorCommand.FileEntry file) {}
            List<CertCandidate> certCandidates = new ArrayList<>();
            for (ApplyInstructorCommand.FileEntry certFile : command.certificateFiles()) {
                OcrPort.OcrResult ocrResult = ocrPort.extractCertificateInfo(certFile.fileBytes(), certFile.fileName());
                if (ocrResult.success()) {
                    certCandidates.add(new CertCandidate(ocrResult, certFile));
                }
            }

            if (certCandidates.isEmpty()) {
                throw new BusinessException(ErrorCode.CERTIFICATE_OCR_FAILED);
            }

            // 이력서 파일 형식 검증
            String resumeFileName = command.resumeFile().fileName();
            if (resumeFileName == null || !resumeFileName.toLowerCase().endsWith(".docx")) {
                throw new BusinessException(ErrorCode.RESUME_INVALID_FORMAT);
            }

            // 이력서 docx - 주요 이력 추출
            List<String> mainCareers = docxPort.extractMainCareers(command.resumeFile().fileBytes());
            if (mainCareers.isEmpty()) {
                throw new BusinessException(ErrorCode.RESUME_PARSE_FAILED);
            }

            // 모든 검증 통과 후 S3 업로드 한꺼번에 수행 (실패 시 보상 삭제)
            List<String> uploadedKeys = new ArrayList<>();
            try {
                List<InstructorCertification> certifications = new ArrayList<>();
                for (CertCandidate candidate : certCandidates) {
                    String certFileKey = fileStoragePort.storePrivate(
                            candidate.file().fileBytes(),
                            candidate.file().fileName(),
                            "instructor-applications/certificates"
                    );
                    uploadedKeys.add(certFileKey);
                    certifications.add(InstructorCertification.of(
                            candidate.ocr().certificationName(),
                            candidate.ocr().issuedBy(),
                            certFileKey
                    ));
                }

                String profileImageKey = fileStoragePort.storePrivate(
                        command.profileImage().fileBytes(),
                        command.profileImage().fileName(),
                        "instructor-applications/profile"
                );
                uploadedKeys.add(profileImageKey);

                String resumeFileKey = fileStoragePort.storePrivate(
                        command.resumeFile().fileBytes(),
                        command.resumeFile().fileName(),
                        "instructor-applications/resume"
                );
                uploadedKeys.add(resumeFileKey);

                InstructorApplication application = InstructorApplication.create(
                        command.userId(),
                        command.bio(),
                        command.motivationLetter(),
                        command.categoryId(),
                        command.portfolioUrl(),
                        profileImageKey,
                        resumeFileKey,
                        mainCareers,
                        certifications
                );

                instructorApplicationRepository.save(application);
            } catch (Exception e) {
                uploadedKeys.forEach(key -> {
                    try {
                        fileStoragePort.deleteFromDocs(key);
                    } catch (Exception deleteEx) {
                        log.error("[S3 보상] 파일 삭제 실패 - key: {}", key, deleteEx);
                    }
                });
                throw e;
            }
            meterRegistry.counter("instructor.application.total", "status", "success", "reason", "NONE").increment();
        } catch (BusinessException e) {
            meterRegistry.counter("instructor.application.total", "status", "failure", "reason", e.getErrorCode().name()).increment();
            throw e;
        } catch (Exception e) {
            meterRegistry.counter("instructor.application.total", "status", "failure", "reason", "SERVER_ERROR").increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("instructor.application.duration")
                    .description("강사 신청 처리 시간")
                    .register(meterRegistry));
        }
    }

    @Override
    public void approveInstructor(Long applicationId) {
        InstructorApplication application = instructorApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        application.approve();
        instructorApplicationRepository.save(application);

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.promoteToInstructor();
        userRepository.save(user);

        eventPublisher.publishEvent(new InstructorApprovedEvent(
                user.getId(),
                user.getName(),
                user.getEmail()
        ));
    }

    @Override
    public void rejectInstructor(Long applicationId, RejectionCategory rejectionCategory, String rejectionReason) {
        InstructorApplication application = instructorApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        application.reject(rejectionCategory, rejectionReason);
        instructorApplicationRepository.save(application);
    }
}

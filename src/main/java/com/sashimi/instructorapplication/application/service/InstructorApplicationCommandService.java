package com.sashimi.instructorapplication.application.service;

import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.storage.FileSignatureValidator;
import com.sashimi.global.storage.FileStoragePort;
import com.sashimi.instructorapplication.application.command.ApplyInstructorCommand;
import com.sashimi.instructorapplication.application.event.InstructorApprovedEvent;
import com.sashimi.instructorapplication.application.event.InstructorAppliedEvent;
import com.sashimi.instructorapplication.application.event.InstructorRejectedEvent;
import com.sashimi.instructorapplication.application.port.DocxPort;
import com.sashimi.instructorapplication.application.usecase.InstructorApplicationCommandUseCase;
import com.sashimi.instructorapplication.domain.model.ApprovalStatus;
import com.sashimi.instructorapplication.domain.model.InstructorApplication;
import com.sashimi.instructorapplication.domain.model.InstructorCertification;
import com.sashimi.instructorapplication.domain.model.RejectionCategory;
import com.sashimi.instructorapplication.domain.repository.InstructorApplicationRepository;
import com.sashimi.instructorapplication.domain.repository.PendingCertification;
import com.sashimi.certificate.application.port.OcrPort;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
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
    private final Executor instructorApplicationExecutor;

    public InstructorApplicationCommandService(
            InstructorApplicationRepository instructorApplicationRepository,
            UserRepository userRepository,
            OcrPort ocrPort,
            DocxPort docxPort,
            FileStoragePort fileStoragePort,
            CategoryRepository categoryRepository,
            ApplicationEventPublisher eventPublisher,
            MeterRegistry meterRegistry,
            @Qualifier("instructorApplicationExecutor") Executor instructorApplicationExecutor
    ) {
        this.instructorApplicationRepository = instructorApplicationRepository;
        this.userRepository = userRepository;
        this.ocrPort = ocrPort;
        this.docxPort = docxPort;
        this.fileStoragePort = fileStoragePort;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.meterRegistry = meterRegistry;
        this.instructorApplicationExecutor = instructorApplicationExecutor;
    }

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

            // 파일 형식 검증 (매직바이트) - OCR/Apache POI 파싱 이전에 즉시 차단
            for (ApplyInstructorCommand.FileEntry certFile : command.certificateFiles()) {
                if (!FileSignatureValidator.isJpegPngOrPdf(certFile.fileBytes())) {
                    throw new BusinessException(ErrorCode.CERTIFICATE_FILE_INVALID_TYPE);
                }
            }
            if (!FileSignatureValidator.isJpegPngOrPdf(command.profileImage().fileBytes())) {
                throw new BusinessException(ErrorCode.CERTIFICATE_FILE_INVALID_TYPE);
            }
            if (!FileSignatureValidator.isDocx(command.resumeFile().fileBytes())) {
                throw new BusinessException(ErrorCode.RESUME_INVALID_FORMAT);
            }

            // 강사 지원은 세부 카테고리 제한 없이 대분류만 선택하므로 mainCategoryId 기준으로 검증
            if (!categoryRepository.existsByMainCategoryId(command.categoryId())) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            // 중복 신청 방지 - S3 업로드 전에 체크해야 orphan 파일 방지
            boolean alreadyApplied = instructorApplicationRepository
                    .existsByUserIdAndApprovalStatus(command.userId(), ApprovalStatus.PENDING);
            if (alreadyApplied) {
                throw new BusinessException(ErrorCode.ALREADY_APPLIED);
            }

            boolean alreadyInstructor = instructorApplicationRepository
                    .existsByUserIdAndApprovalStatus(command.userId(), ApprovalStatus.APPROVED);
            if (alreadyInstructor) {
                throw new BusinessException(ErrorCode.ALREADY_INSTRUCTOR);
            }

            // 자격증 OCR 검증 + 이력서 주요 이력 추출 - 서로 무관한 작업이라 병렬 실행
            record CertCandidate(OcrPort.OcrResult ocr, ApplyInstructorCommand.FileEntry file) {}

            CompletableFuture<List<CertCandidate>> certFuture = CompletableFuture.supplyAsync(() -> {
                List<CertCandidate> candidates = new ArrayList<>();
                for (ApplyInstructorCommand.FileEntry certFile : command.certificateFiles()) {
                    OcrPort.OcrResult ocrResult = ocrPort.extractCertificateInfo(certFile.fileBytes(), certFile.fileName());
                    if (ocrResult.certificationNumber() != null) {
                        candidates.add(new CertCandidate(ocrResult, certFile));
                    }
                }
                return candidates;
            }, instructorApplicationExecutor);

            CompletableFuture<List<String>> careersFuture = CompletableFuture.supplyAsync(
                    () -> docxPort.extractMainCareers(command.resumeFile().fileBytes()),
                    instructorApplicationExecutor
            );

            List<CertCandidate> certCandidates = joinUnwrapped(certFuture);
            List<String> mainCareers = joinUnwrapped(careersFuture);

            if (certCandidates.isEmpty()) {
                throw new BusinessException(ErrorCode.CERTIFICATE_OCR_FAILED);
            }
            if (mainCareers.isEmpty()) {
                throw new BusinessException(ErrorCode.RESUME_PARSE_FAILED);
            }

            // 모든 검증 통과 후 S3 업로드 3종(자격증 N개+프로필+이력서) 병렬 수행 (실패 시 보상 삭제)
            // 작업 "제출" 자체(supplyAsync 호출)도 try 안에서 해야 함 - executor 포화로 제출이
            // RejectedExecutionException을 던지는 경우, 그 전에 이미 제출된 업로드도 보상 대상이라서
            List<CompletableFuture<String>> allUploadFutures = new ArrayList<>();
            List<CompletableFuture<String>> certUploadFutures = new ArrayList<>();
            List<String> certFileKeys;
            String profileImageKey;
            String resumeFileKey;
            try {
                for (CertCandidate candidate : certCandidates) {
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
                            fileStoragePort.storePrivate(
                                    candidate.file().fileBytes(),
                                    candidate.file().fileName(),
                                    "instructor-applications/certificates"
                            ), instructorApplicationExecutor);
                    certUploadFutures.add(future);
                    allUploadFutures.add(future);
                }

                CompletableFuture<String> profileUploadFuture = CompletableFuture.supplyAsync(() ->
                        fileStoragePort.storePrivate(
                                command.profileImage().fileBytes(),
                                command.profileImage().fileName(),
                                "instructor-applications/profile"
                        ), instructorApplicationExecutor);
                allUploadFutures.add(profileUploadFuture);

                CompletableFuture<String> resumeUploadFuture = CompletableFuture.supplyAsync(() ->
                        fileStoragePort.storePrivate(
                                command.resumeFile().fileBytes(),
                                command.resumeFile().fileName(),
                                "instructor-applications/resume"
                        ), instructorApplicationExecutor);
                allUploadFutures.add(resumeUploadFuture);

                CompletableFuture.allOf(allUploadFutures.toArray(new CompletableFuture[0])).join();

                certFileKeys = certUploadFutures.stream().map(CompletableFuture::join).toList();
                profileImageKey = profileUploadFuture.join();
                resumeFileKey = resumeUploadFuture.join();
            } catch (RuntimeException e) {
                compensateSucceeded(allUploadFutures);
                if (e instanceof CompletionException && e.getCause() instanceof RuntimeException re) {
                    throw re;
                }
                throw e;
            }

            List<InstructorCertification> certifications = new ArrayList<>();
            for (int i = 0; i < certCandidates.size(); i++) {
                certifications.add(InstructorCertification.of(
                        certCandidates.get(i).ocr().certificationName(),
                        certCandidates.get(i).ocr().issuedBy(),
                        certFileKeys.get(i),
                        certCandidates.get(i).ocr().certificationNumber()
                ));
            }

            try {
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
                List<String> uploadedKeys = new ArrayList<>(certFileKeys);
                uploadedKeys.add(profileImageKey);
                uploadedKeys.add(resumeFileKey);
                uploadedKeys.forEach(key -> {
                    try {
                        fileStoragePort.deleteFromDocs(key);
                    } catch (Exception deleteEx) {
                        log.error("[S3 보상] 파일 삭제 실패 - key: {}", key, deleteEx);
                    }
                });
                if (e instanceof DataIntegrityViolationException) {
                    throw new BusinessException(ErrorCode.ALREADY_APPLIED);
                }
                throw e;
            }
            User applicant = userRepository.findById(command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            eventPublisher.publishEvent(new InstructorAppliedEvent(
                    applicant.getId(),
                    applicant.getName(),
                    applicant.getEmail()
            ));

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

    private <T> T joinUnwrapped(CompletableFuture<T> future) {
        try {
            return future.join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            }
            throw e;
        }
    }

    private void compensateSucceeded(List<CompletableFuture<String>> uploadFutures) {
        uploadFutures.stream()
                .filter(f -> f.isDone() && !f.isCompletedExceptionally())
                .forEach(f -> {
                    String key = f.join();
                    try {
                        fileStoragePort.deleteFromDocs(key);
                    } catch (Exception deleteEx) {
                        log.error("[S3 보상] 파일 삭제 실패 - key: {}", key, deleteEx);
                    }
                });
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

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        eventPublisher.publishEvent(new InstructorRejectedEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                rejectionCategory,
                rejectionReason
        ));
    }

    @Override
    public byte[] generateVerificationExcel() {
        List<PendingCertification> pending = instructorApplicationRepository.findAllPendingCertificationsWithNumber();

        List<Long> userIds = pending.stream()
                .map(PendingCertification::userId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> namesByUserId = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("성명");
            header.createCell(1).setCellValue("자격증번호");

            List<Long> includedCertificationIds = new ArrayList<>();
            int rowIndex = 1;
            for (PendingCertification p : pending) {
                String name = namesByUserId.get(p.userId());
                if (name == null) {
                    continue;
                }
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(p.certificationNumber());
                includedCertificationIds.add(p.certificationId());
            }

            if (!includedCertificationIds.isEmpty()) {
                instructorApplicationRepository.markCertificationsSubmitted(includedCertificationIds);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("검증 엑셀 생성에 실패했습니다.", e);
        }
    }
}

package com.sashimi.resume.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResumeQueryServiceTest {

    private ResumeRepository resumeRepository;
    private ResumeQueryService resumeQueryService;

    @BeforeEach
    void setUp() {
        // Query Service는 ResumeRepository만 의존하므로 Repository를 Mock으로 대체
        resumeRepository = mock(ResumeRepository.class);
        resumeQueryService = new ResumeQueryService(resumeRepository);
    }

    @Test
    void getMyResumes() {
        // given
        // 로그인한 사용자가 작성한 이력서 목록이 있다고 가정
        Resume resume = new Resume(
                1L,
                1L,
                "Backend Resume",
                "{\"summary\":\"Java backend developer\"}"
        );

        when(resumeRepository.findAllByUserId(1L))
                .thenReturn(List.of(resume));

        // when
        List<Resume> result = resumeQueryService.getMyResumes(1L);

        // then
        // 사용자의 이력서 목록이 그대로 반환되는지 확인
        assertThat(result).hasSize(1);
        assertThat(result.get(0).resumeId()).isEqualTo(1L);
        // 사용자 ID 기준으로 이력서 목록을 조회했는지 검증
        verify(resumeRepository).findAllByUserId(1L);
    }

    @Test
    void getResume() {
        // given
        // 특정 사용자가 소유한 특정 이력서를 조회할 수 있는 상황
        Resume resume = new Resume(
                1L,
                1L,
                "Backend Resume",
                "{\"summary\":\"Java backend developer\"}"
        );

        when(resumeRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(resume));

        // when
        Resume result = resumeQueryService.getResume(1L, 1L);

        // then
        // 조회된 이력서가 요청한 사용자와 이력서 ID에 맞는지 확인
        assertThat(result.resumeId()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(1L);
        // 본인 이력서만 조회해야 하므로 resumeId와 userId를 함께 사용했는지 검증
        verify(resumeRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getResumeThrowsWhenNotFound() {
        // given
        // 요청한 사용자의 해당 이력서가 존재하지 않는 상황
        when(resumeRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        // 이력서를 찾지 못하면 RESUME_NOT_FOUND BusinessException이 발생
        assertThatThrownBy(() -> resumeQueryService.getResume(1L, 999L))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESUME_NOT_FOUND)
                );

        // 본인 이력서 조회 조건으로 resumeId와 userId를 함께 사용했는지 검증
        verify(resumeRepository).findByIdAndUserId(999L, 1L);
    }
}
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
        resumeRepository = mock(ResumeRepository.class);
        resumeQueryService = new ResumeQueryService(resumeRepository);
    }

    @Test
    void getMyResumes() {
        // given
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
        assertThat(result).hasSize(1);
        assertThat(result.get(0).resumeId()).isEqualTo(1L);
        verify(resumeRepository).findAllByUserId(1L);
    }

    @Test
    void getResume() {
        // given
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
        assertThat(result.resumeId()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(1L);
        verify(resumeRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void getResumeThrowsWhenNotFound() {
        // given
        when(resumeRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> resumeQueryService.getResume(1L, 999L))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESUME_NOT_FOUND)
                );

        verify(resumeRepository).findByIdAndUserId(999L, 1L);
    }
}

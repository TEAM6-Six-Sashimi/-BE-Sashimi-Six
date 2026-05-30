package com.sashimi.user.application.service;

import com.sashimi.user.application.event.UserWithdrawnEvent;
import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("탈퇴 최종 처리 테스트")
class UserWithdrawalFinalizationServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ApplicationEventPublisher eventPublisher;
    private UserWithdrawalFinalizationService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new UserWithdrawalFinalizationService(userRepository, passwordEncoder, eventPublisher);

        when(passwordEncoder.encode(any())).thenReturn("hashed-random-password");
    }

    @Test
    @DisplayName("탈퇴 후 1년이 지난 유저는 개인정보가 익명화된다")
    void user_data_is_anonymized_after_one_year_from_withdrawal() {
        // given: 1년 전에 탈퇴한 유저
        User inactiveUser = userDeactivatedYearsAgo(1, 1L);
        when(userRepository.findAllByStatusAndDeactivatedAtBefore(any(), any()))
                .thenReturn(List.of(inactiveUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.finalizeExpiredWithdrawals();

        // then: 개인정보 익명화 확인
        assertThat(inactiveUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(inactiveUser.getLoginId()).isEqualTo("d1");
        assertThat(inactiveUser.getEmail()).isEqualTo("deleted_1@deleted.local");
        assertThat(inactiveUser.getName()).isEqualTo("탈퇴회원");
    }

    @Test
    @DisplayName("탈퇴 후 1년이 지난 유저는 UserWithdrawnEvent가 발행된다")
    void withdrawn_event_is_published_after_one_year() {
        // given
        User inactiveUser = userDeactivatedYearsAgo(1, 2L);
        when(userRepository.findAllByStatusAndDeactivatedAtBefore(any(), any()))
                .thenReturn(List.of(inactiveUser));
        when(userRepository.save(any())).thenReturn(inactiveUser);

        // when
        service.finalizeExpiredWithdrawals();

        // then: 이벤트 발행 확인
        ArgumentCaptor<UserWithdrawnEvent> captor = ArgumentCaptor.forClass(UserWithdrawnEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("탈퇴 후 1년이 지나지 않은 유저는 처리되지 않는다")
    void user_within_grace_period_is_not_processed() {
        // given: 아직 1년이 안 된 유저는 조회 결과에 포함되지 않음
        when(userRepository.findAllByStatusAndDeactivatedAtBefore(any(), any()))
                .thenReturn(List.of());

        // when
        service.finalizeExpiredWithdrawals();

        // then: 아무것도 처리되지 않음
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("여러 유저가 동시에 만료되면 모두 처리된다")
    void multiple_expired_users_are_all_processed() {
        // given
        User user1 = userDeactivatedYearsAgo(1, 3L);
        User user2 = userDeactivatedYearsAgo(2, 4L);
        when(userRepository.findAllByStatusAndDeactivatedAtBefore(any(), any()))
                .thenReturn(List.of(user1, user2));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        service.finalizeExpiredWithdrawals();

        // then: 둘 다 처리됨
        verify(userRepository, times(2)).save(any());
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
        assertThat(user1.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user2.getStatus()).isEqualTo(UserStatus.DELETED);
    }

    private User userDeactivatedYearsAgo(int years, Long id) {
        return new User(
                id, "테스트유저", "testuser" + id, "password",
                "test" + id + "@test.com", "010-0000-0000",
                LocalDate.of(1990, 1, 1),
                Role.STUDENT, UserStatus.INACTIVE, true,
                "REF00" + id, List.of(),
                LocalDateTime.now().minusYears(years).minusDays(1)
        );
    }
}

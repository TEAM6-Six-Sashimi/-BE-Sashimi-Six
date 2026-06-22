package com.sashimi.user.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.application.event.UserPasswordChangedEvent;
import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.presentation.api.response.ChangePasswordResult;
import com.sashimi.user.presentation.api.response.WithdrawUserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

class UserAccountServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RefreshService refreshService;
    private ApplicationEventPublisher eventPublisher;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshService = mock(RefreshService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        userAccountService = new UserAccountService(
                userRepository,
                passwordEncoder,
                refreshService,
                eventPublisher
        );
    }

    @Test
    void changePasswordPublishesUserPasswordChangedEvent() {
        // given
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedCurrentPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // when
        ChangePasswordResult result = userAccountService.changePassword(
                new ChangePasswordCommand(
                        1L,
                        "currentPassword",
                        "newPassword123"
                )
        );

        // then
        assertThat(result.passwordChanged()).isTrue();
        assertThat(result.requiresLogin()).isTrue();
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");

        ArgumentCaptor<UserPasswordChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(UserPasswordChangedEvent.class);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(1L);
        assertThat(eventCaptor.getValue().email()).isEqualTo("test@example.com");
        verify(refreshService, never()).deleteByUser(any());
    }

    @Test
    void changePasswordThrowsWhenCurrentPasswordDoesNotMatch() {
        // given
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedCurrentPassword")).thenReturn(false);

        // when
        BusinessException exception = catchThrowableOfType(
                () -> userAccountService.changePassword(
                        new ChangePasswordCommand(
                                1L,
                                "wrongPassword",
                                "newPassword123"
                        )
                ),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CURRENT_PASSWORD);
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void withdrawDeactivatesUserAndDeletesRefreshToken() {
        // given
        User user = activeUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        // when
        WithdrawUserResult result = userAccountService.withdraw(
                new WithdrawUserCommand(
                        1L,
                        "currentPassword"
                )
        );

        // then
        assertThat(result.status()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.getDeactivatedAt()).isNotNull();

        verify(userRepository).save(user);
        verify(refreshService).deleteByUser(user);
    }

    private User activeUser() {
        return new User(
                1L,
                "테스트회원",
                "testuser",
                "encodedCurrentPassword",
                "test@example.com",
                "010-9999-9999",
                LocalDate.of(2000, 1, 1),
                Role.STUDENT,
                UserStatus.ACTIVE,
                true,
                "ABC12345",
                List.of(1L),
                null,
                false, false, false
        );
    }
}

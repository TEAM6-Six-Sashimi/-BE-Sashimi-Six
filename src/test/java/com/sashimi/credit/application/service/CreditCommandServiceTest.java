package com.sashimi.credit.application.service;

import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("추천인 크레딧 지급 테스트")
class CreditCommandServiceTest {

    private CreditRepository creditRepository;
    private CreditCommandService creditCommandService;

    @BeforeEach
    void setUp() {
        creditRepository = mock(CreditRepository.class);
        creditCommandService = new CreditCommandService(creditRepository);
    }

    @Test
    @DisplayName("추천인 코드로 가입 시 새 유저는 5000 크레딧을 받는다")
    void newUser_receives_5000_credits_when_signed_up_with_referral_code() {
        // given
        Long newUserId = 10L;
        Long referrerUserId = 1L;

        // 새 유저 크레딧 없음
        when(creditRepository.findByUserId(newUserId)).thenReturn(Optional.empty());
        // 추천인 크레딧 존재
        when(creditRepository.findByUserIdForUpdate(referrerUserId))
                .thenReturn(Optional.of(Credit.create(referrerUserId, 0L)));
        when(creditRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        creditCommandService.grantReferralSignupRewards(
                new GrantReferralSignupRewardCommand(newUserId, referrerUserId)
        );

        // then: 새 유저 5000 크레딧으로 생성 검증
        verify(creditRepository).save(argThat(credit ->
                credit.getUserId().equals(newUserId) &&
                credit.getBalance().equals(CreditCommandService.NEW_USER_REFERRAL_REWARD)
        ));
    }

    @Test
    @DisplayName("추천인 코드로 가입 시 추천인은 1000 크레딧을 받는다")
    void referrer_receives_1000_credits_when_their_code_is_used() {
        // given
        Long newUserId = 10L;
        Long referrerUserId = 1L;
        Long referrerInitialBalance = 3000L;

        when(creditRepository.findByUserId(newUserId)).thenReturn(Optional.empty());
        Credit referrerCredit = Credit.create(referrerUserId, referrerInitialBalance);
        when(creditRepository.findByUserIdForUpdate(referrerUserId))
                .thenReturn(Optional.of(referrerCredit));
        when(creditRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        creditCommandService.grantReferralSignupRewards(
                new GrantReferralSignupRewardCommand(newUserId, referrerUserId)
        );

        // then: 추천인 잔액 = 3000 + 1000 = 4000
        assertThat(referrerCredit.getBalance())
                .isEqualTo(referrerInitialBalance + CreditCommandService.REFERRER_REWARD);
    }

    @Test
    @DisplayName("현재 지급 금액 확인: 새 유저 5000, 추천인 1000")
    void verify_current_reward_amounts() {
        assertThat(CreditCommandService.NEW_USER_REFERRAL_REWARD).isEqualTo(5000L);
        assertThat(CreditCommandService.REFERRER_REWARD).isEqualTo(1000L);
    }
}

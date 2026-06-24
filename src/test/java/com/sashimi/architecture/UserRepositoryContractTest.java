package com.sashimi.architecture;

import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 계약 테스트.
 * 이 테스트를 상속하는 구현체는 모두 동일한 동작을 보장해야 한다.
 */
public abstract class UserRepositoryContractTest {

    protected UserRepository userRepository;

    protected abstract UserRepository createRepository();

    @BeforeEach
    void setUp() {
        userRepository = createRepository();
    }

    private User sampleUser(String loginId, String email) {
        return User.createStudent(
                "테스트유저", loginId, "password123",
                email, "010-0000-0000",
                LocalDate.of(2000, 1, 1),
                "REF001", List.of(),
                false, false, false
        );
    }

    @Test
    @DisplayName("저장한 유저를 ID로 조회할 수 있다")
    void save_and_findById() {
        User saved = userRepository.save(sampleUser("user1", "user1@test.com"));

        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLoginId()).isEqualTo("user1");
    }

    @Test
    @DisplayName("저장한 유저를 loginId로 조회할 수 있다")
    void findByLoginId() {
        userRepository.save(sampleUser("user2", "user2@test.com"));

        Optional<User> found = userRepository.findByLoginId("user2");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user2@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 loginId 조회 시 빈 Optional을 반환한다")
    void findByLoginId_notFound() {
        Optional<User> found = userRepository.findByLoginId("없는유저");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("저장한 유저를 email로 조회할 수 있다")
    void findByEmail() {
        userRepository.save(sampleUser("user3", "user3@test.com"));

        Optional<User> found = userRepository.findByEmail("user3@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getLoginId()).isEqualTo("user3");
    }

    @Test
    @DisplayName("loginId 중복 여부를 확인할 수 있다")
    void existsByLoginId() {
        userRepository.save(sampleUser("user4", "user4@test.com"));

        assertThat(userRepository.existsByLoginId("user4")).isTrue();
        assertThat(userRepository.existsByLoginId("없는유저")).isFalse();
    }

    @Test
    @DisplayName("email 중복 여부를 확인할 수 있다")
    void existsByEmail() {
        userRepository.save(sampleUser("user5", "user5@test.com"));

        assertThat(userRepository.existsByEmail("user5@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("없는@test.com")).isFalse();
    }

    @Test
    @DisplayName("저장 시 ID가 자동 부여된다")
    void save_assignsId() {
        User saved = userRepository.save(sampleUser("user6", "user6@test.com"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("유저 정보를 수정 후 저장하면 반영된다")
    void save_updatesExistingUser() {
        User saved = userRepository.save(sampleUser("user7", "user7@test.com"));
        saved.updateProfile("010-1234-5678", false, false, false);
        userRepository.save(saved);

        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("비활성화되고 특정 시간 이전에 탈퇴한 유저 목록을 조회할 수 있다")
    void findAllByStatusAndDeactivatedAtBefore() {
        User user = userRepository.save(sampleUser("user8", "user8@test.com"));
        user.deactivate();
        userRepository.save(user);

        List<User> result = userRepository.findAllByStatusAndDeactivatedAtBefore(
                UserStatus.INACTIVE,
                LocalDateTime.now().plusSeconds(1)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLoginId()).isEqualTo("user8");
    }
}

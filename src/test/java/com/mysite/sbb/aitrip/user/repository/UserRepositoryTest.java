package com.mysite.sbb.aitrip.user.repository;

import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장")
    void save() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        // given
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build());

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmail() {
        // given
        userRepository.save(User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build());

        // when & then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }
}

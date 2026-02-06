package com.mysite.sbb.aitrip.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User 엔티티 테스트")
class UserTest {

    @Test
    @DisplayName("User 엔티티 생성 - Builder 패턴")
    void createUser() {
        // given
        String email = "test@example.com";
        String password = "encoded_password";
        String name = "홍길동";

        // when
        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(User.Role.ROLE_USER)
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getRole()).isEqualTo(User.Role.ROLE_USER);
    }
}

package com.mysite.sbb.aitrip.user.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 조회 - 성공")
    void getUser_success() {
        // given
        User user = createUser(1L, "test@example.com", "홍길동");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUser(1L);

        // then
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.name()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 사용자")
    void getUser_notFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    private User createUser(Long id, String email, String name) {
        User user = User.builder()
                .email(email)
                .password("encoded_password")
                .name(name)
                .role(User.Role.ROLE_USER)
                .build();
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}

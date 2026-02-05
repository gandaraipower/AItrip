package com.mysite.sbb.aitrip.auth.service;

import com.mysite.sbb.aitrip.auth.dto.LoginRequest;
import com.mysite.sbb.aitrip.auth.dto.SignupRequest;
import com.mysite.sbb.aitrip.auth.dto.TokenResponse;
import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.global.security.jwt.JwtTokenProvider;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("회원가입 - 성공")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "홍길동");
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode("Password1!")).willReturn("encoded");

        User savedUser = createUser(1L, "test@example.com", "홍길동");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserResponse result = authService.signup(request);

        // then
        assertThat(result.email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복")
    void signup_duplicateEmail() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "홍길동");
        given(userRepository.existsByEmail("test@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
                });
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password1!");
        User user = createUser(1L, "test@example.com", "홍길동");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Password1!", user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(anyString())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(604800000L);

        // when
        TokenResponse result = authService.login(request);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호")
    void login_invalidPassword() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "WrongPass1!");
        User user = createUser(1L, "test@example.com", "홍길동");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("WrongPass1!", user.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
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

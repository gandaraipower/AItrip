package com.mysite.sbb.aitrip.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.aitrip.auth.dto.LoginRequest;
import com.mysite.sbb.aitrip.auth.dto.SignupRequest;
import com.mysite.sbb.aitrip.auth.dto.TokenResponse;
import com.mysite.sbb.aitrip.auth.service.AuthService;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/signup - 회원가입 성공")
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "홍길동");
        UserResponse response = new UserResponse(1L, "test@example.com", "홍길동",
                "ROLE_USER", LocalDateTime.now(), LocalDateTime.now());
        given(authService.signup(any(SignupRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 로그인 성공")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password1!");
        TokenResponse response = new TokenResponse("access-token", "refresh-token");
        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /api/auth/signup - 유효성 검증 실패")
    void signup_validationFail() throws Exception {
        // given
        SignupRequest request = new SignupRequest("invalid-email", "weak", "");

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

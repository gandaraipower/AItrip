package com.mysite.sbb.aitrip.auth.controller;

import com.mysite.sbb.aitrip.auth.dto.*;
import com.mysite.sbb.aitrip.auth.service.AuthService;
import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.security.CustomUserDetails;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/api/auth/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authService.signup(request)));
    }

    // 로그인 API
    @PostMapping("/api/auth/login")
    @Operation(summary = "로그인")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(authService.login(request)));
    }

    // 토큰 갱신 API
    @PostMapping("/api/auth/refresh")
    @Operation(summary = "토큰 갱신")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(authService.refresh(request)));
    }

    // 로그아웃 API
    @PostMapping("/api/auth/logout")
    @Operation(summary = "로그아웃", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String token = resolveToken(request);
        authService.logout(token, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

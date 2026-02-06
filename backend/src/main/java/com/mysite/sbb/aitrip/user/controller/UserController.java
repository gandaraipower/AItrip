package com.mysite.sbb.aitrip.user.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.security.CustomUserDetails;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import com.mysite.sbb.aitrip.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    // 내 정보 조회 API
    @GetMapping("/api/users/me")
    @Operation(summary = "내 정보 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(userService.getUser(userDetails.getUserId())));
    }
}

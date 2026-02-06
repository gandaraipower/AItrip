package com.mysite.sbb.aitrip.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신 요청")
public record RefreshRequest(
        @Schema(description = "Refresh Token")
        @NotBlank(message = "Refresh Token은 필수입니다.")
        String refreshToken
) {
}

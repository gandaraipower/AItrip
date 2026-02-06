package com.mysite.sbb.aitrip.user.dto;

import com.mysite.sbb.aitrip.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "사용자 응답")
public record UserResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "역할", example = "ROLE_USER")
        String role,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }
}

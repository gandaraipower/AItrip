package com.mysite.sbb.aitrip.auth.dto;

import com.mysite.sbb.aitrip.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "비밀번호 (대소문자, 숫자, 특수문자 각 1개 이상, 8~20자)", example = "Password1!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각 1개 이상 포함해야 합니다.")
        String password,

        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
        String name
) {
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(User.Role.ROLE_USER)
                .build();
    }
}

package com.mysite.sbb.aitrip.trip.dto;

import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "여행 생성/수정 요청")
public record TripRequest(
        @Schema(description = "여행 제목", example = "봄 여행")
        @NotBlank(message = "여행 제목은 필수입니다.")
        String title,

        @Schema(description = "여행 지역", example = "서울")
        @NotBlank(message = "여행 지역은 필수입니다.")
        String region,

        @Schema(description = "사용자 입력 스타일", example = "감성적인 카페와 소품샵 위주")
        String style,

        @Schema(description = "여행 페이스", example = "NORMAL")
        @NotNull(message = "여행 페이스는 필수입니다.")
        TripStyle tripStyle,

        @Schema(description = "출발일", example = "2025-03-10")
        @NotNull(message = "출발일은 필수입니다.")
        LocalDate startDate,

        @Schema(description = "귀가일", example = "2025-03-13")
        @NotNull(message = "귀가일은 필수입니다.")
        LocalDate endDate
) {
    public Trip toEntity(User user) {
        return Trip.builder()
                .user(user)
                .title(title)
                .region(region)
                .style(style)
                .tripStyle(tripStyle)
                .startDate(startDate)
                .endDate(endDate)
                .status(TripStatus.DRAFT)
                .build();
    }
}

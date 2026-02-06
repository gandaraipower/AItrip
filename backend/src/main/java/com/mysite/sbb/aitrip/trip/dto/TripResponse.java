package com.mysite.sbb.aitrip.trip.dto;

import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "여행 응답")
public record TripResponse(
        @Schema(description = "여행 ID", example = "1")
        Long id,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "여행 제목", example = "봄 여행")
        String title,

        @Schema(description = "여행 지역", example = "서울")
        String region,

        @Schema(description = "사용자 입력 스타일")
        String style,

        @Schema(description = "여행 페이스")
        TripStyle tripStyle,

        @Schema(description = "출발일")
        LocalDate startDate,

        @Schema(description = "귀가일")
        LocalDate endDate,

        @Schema(description = "상태")
        TripStatus status,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static TripResponse from(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getUser().getId(),
                trip.getTitle(),
                trip.getRegion(),
                trip.getStyle(),
                trip.getTripStyle(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getStatus(),
                trip.getCreatedAt(),
                trip.getModifiedAt()
        );
    }
}

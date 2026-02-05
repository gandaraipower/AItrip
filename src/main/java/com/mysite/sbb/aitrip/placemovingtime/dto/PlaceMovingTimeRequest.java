package com.mysite.sbb.aitrip.placemovingtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "이동시간 등록 요청")
public record PlaceMovingTimeRequest(
        @Schema(description = "출발 장소 ID", example = "1")
        @NotNull(message = "출발 장소 ID는 필수입니다.")
        Long fromPlaceId,

        @Schema(description = "도착 장소 ID", example = "2")
        @NotNull(message = "도착 장소 ID는 필수입니다.")
        Long toPlaceId,

        @Schema(description = "거리(km)", example = "3.5")
        BigDecimal distanceKm,

        @Schema(description = "이동시간(분)", example = "15")
        Integer timeMinutes,

        @Schema(description = "이동수단", example = "도보")
        String transportType
) {
}

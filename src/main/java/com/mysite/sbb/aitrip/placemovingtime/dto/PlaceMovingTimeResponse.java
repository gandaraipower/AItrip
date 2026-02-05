package com.mysite.sbb.aitrip.placemovingtime.dto;

import com.mysite.sbb.aitrip.placemovingtime.domain.PlaceMovingTime;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "이동시간 응답")
public record PlaceMovingTimeResponse(
        @Schema(description = "ID", example = "1")
        Long id,

        @Schema(description = "출발 장소 ID")
        Long fromPlaceId,

        @Schema(description = "도착 장소 ID")
        Long toPlaceId,

        @Schema(description = "거리(km)")
        BigDecimal distanceKm,

        @Schema(description = "이동시간(분)")
        Integer timeMinutes,

        @Schema(description = "이동수단")
        String transportType,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static PlaceMovingTimeResponse from(PlaceMovingTime pmt) {
        return new PlaceMovingTimeResponse(
                pmt.getId(),
                pmt.getFromPlace().getId(),
                pmt.getToPlace().getId(),
                pmt.getDistanceKm(),
                pmt.getTimeMinutes(),
                pmt.getTransportType(),
                pmt.getCreatedAt(),
                pmt.getModifiedAt()
        );
    }
}

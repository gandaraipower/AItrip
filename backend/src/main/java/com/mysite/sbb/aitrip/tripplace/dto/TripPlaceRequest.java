package com.mysite.sbb.aitrip.tripplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "여행 장소 추가 요청")
public record TripPlaceRequest(
        @Schema(description = "장소 ID", example = "1")
        @NotNull(message = "장소 ID는 필수입니다.")
        Long placeId,

        @Schema(description = "선택 여부", example = "true")
        Boolean isSelected
) {
}

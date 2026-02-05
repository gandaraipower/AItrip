package com.mysite.sbb.aitrip.placecrowddata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "혼잡도 등록 요청")
public record PlaceCrowdDataRequest(
        @Schema(description = "장소 ID", example = "1")
        @NotNull(message = "장소 ID는 필수입니다.")
        Long placeId,

        @Schema(description = "요일 (0~6, 일~토)", example = "1")
        @NotNull(message = "요일은 필수입니다.")
        Integer dayOfWeek,

        @Schema(description = "시간 (0~23)", example = "12")
        @NotNull(message = "시간은 필수입니다.")
        Integer hour,

        @Schema(description = "혼잡도 (낮음/중간/높음)", example = "높음")
        String crowdLevel,

        @Schema(description = "대기 위험도 (0~1)", example = "0.75")
        BigDecimal waitingRiskScore,

        @Schema(description = "평균 대기시간(분)", example = "30")
        Integer avgWaitingMin
) {
}

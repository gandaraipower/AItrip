package com.mysite.sbb.aitrip.placecrowddata.dto;

import com.mysite.sbb.aitrip.placecrowddata.domain.PlaceCrowdData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "혼잡도 응답")
public record PlaceCrowdDataResponse(
        @Schema(description = "ID", example = "1")
        Long id,

        @Schema(description = "장소 ID")
        Long placeId,

        @Schema(description = "요일 (0~6)")
        Integer dayOfWeek,

        @Schema(description = "시간 (0~23)")
        Integer hour,

        @Schema(description = "혼잡도")
        String crowdLevel,

        @Schema(description = "대기 위험도")
        BigDecimal waitingRiskScore,

        @Schema(description = "평균 대기시간(분)")
        Integer avgWaitingMin,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static PlaceCrowdDataResponse from(PlaceCrowdData data) {
        return new PlaceCrowdDataResponse(
                data.getId(),
                data.getPlace().getId(),
                data.getDayOfWeek(),
                data.getHour(),
                data.getCrowdLevel(),
                data.getWaitingRiskScore(),
                data.getAvgWaitingMin(),
                data.getCreatedAt(),
                data.getModifiedAt()
        );
    }
}

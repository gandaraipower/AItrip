package com.mysite.sbb.aitrip.placestyletag.dto;

import com.mysite.sbb.aitrip.placestyletag.domain.PlaceStyleTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "장소 스타일 태그 응답")
public record PlaceStyleTagResponse(
        @Schema(description = "태그 ID", example = "1")
        Long id,

        @Schema(description = "장소 ID", example = "1")
        Long placeId,

        @Schema(description = "태그명", example = "감성")
        String tag,

        @Schema(description = "언급 횟수")
        Integer frequency,

        @Schema(description = "중요도")
        BigDecimal weight,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static PlaceStyleTagResponse from(PlaceStyleTag placeStyleTag) {
        return new PlaceStyleTagResponse(
                placeStyleTag.getId(),
                placeStyleTag.getPlace().getId(),
                placeStyleTag.getTag(),
                placeStyleTag.getFrequency(),
                placeStyleTag.getWeight(),
                placeStyleTag.getCreatedAt(),
                placeStyleTag.getModifiedAt()
        );
    }
}

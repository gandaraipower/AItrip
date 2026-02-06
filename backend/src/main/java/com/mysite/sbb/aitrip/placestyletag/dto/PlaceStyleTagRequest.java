package com.mysite.sbb.aitrip.placestyletag.dto;

import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.placestyletag.domain.PlaceStyleTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Schema(description = "장소 스타일 태그 등록 요청")
public record PlaceStyleTagRequest(
        @Schema(description = "태그명", example = "감성")
        @NotBlank(message = "태그명은 필수입니다.")
        String tag,

        @Schema(description = "언급 횟수", example = "15")
        Integer frequency,

        @Schema(description = "중요도 (0~1)", example = "0.85")
        BigDecimal weight
) {
    public PlaceStyleTag toEntity(Place place) {
        return PlaceStyleTag.builder()
                .place(place)
                .tag(tag)
                .frequency(frequency)
                .weight(weight)
                .build();
    }
}

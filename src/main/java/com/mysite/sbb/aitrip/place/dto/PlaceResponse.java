package com.mysite.sbb.aitrip.place.dto;

import com.mysite.sbb.aitrip.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "장소 응답")
public record PlaceResponse(
        @Schema(description = "장소 ID", example = "1")
        Long id,

        @Schema(description = "장소명", example = "블루보틀")
        String name,

        @Schema(description = "지역", example = "서울")
        String region,

        @Schema(description = "카테고리", example = "카페")
        String category,

        @Schema(description = "주소")
        String address,

        @Schema(description = "위도")
        BigDecimal latitude,

        @Schema(description = "경도")
        BigDecimal longitude,

        @Schema(description = "영업시간")
        String operatingHours,

        @Schema(description = "권장 체류시간(분)")
        Integer estimatedStayTime,

        @Schema(description = "이미지 URL")
        String imageUrl,

        @Schema(description = "데이터 출처")
        String source,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getRegion(),
                place.getCategory(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getOperatingHours(),
                place.getEstimatedStayTime(),
                place.getImageUrl(),
                place.getSource(),
                place.getCreatedAt(),
                place.getModifiedAt()
        );
    }
}

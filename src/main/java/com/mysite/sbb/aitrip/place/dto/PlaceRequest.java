package com.mysite.sbb.aitrip.place.dto;

import com.mysite.sbb.aitrip.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Schema(description = "장소 생성/수정 요청")
public record PlaceRequest(
        @Schema(description = "장소명", example = "블루보틀")
        @NotBlank(message = "장소명은 필수입니다.")
        String name,

        @Schema(description = "지역", example = "서울")
        @NotBlank(message = "지역은 필수입니다.")
        String region,

        @Schema(description = "카테고리", example = "카페")
        @NotBlank(message = "카테고리는 필수입니다.")
        String category,

        @Schema(description = "주소", example = "강남구 압구정로 27")
        @NotBlank(message = "주소는 필수입니다.")
        String address,

        @Schema(description = "위도", example = "37.5265")
        BigDecimal latitude,

        @Schema(description = "경도", example = "127.0402")
        BigDecimal longitude,

        @Schema(description = "영업시간", example = "09:00-21:00")
        String operatingHours,

        @Schema(description = "권장 체류시간(분)", example = "45")
        Integer estimatedStayTime,

        @Schema(description = "이미지 URL")
        String imageUrl,

        @Schema(description = "데이터 출처", example = "TourAPI")
        String source
) {
    public Place toEntity() {
        return Place.builder()
                .name(name)
                .region(region)
                .category(category)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .operatingHours(operatingHours)
                .estimatedStayTime(estimatedStayTime)
                .imageUrl(imageUrl)
                .source(source)
                .build();
    }
}

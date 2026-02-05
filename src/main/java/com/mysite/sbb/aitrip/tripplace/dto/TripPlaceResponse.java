package com.mysite.sbb.aitrip.tripplace.dto;

import com.mysite.sbb.aitrip.tripplace.domain.TripPlace;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "여행 장소 응답")
public record TripPlaceResponse(
        @Schema(description = "ID", example = "1")
        Long id,

        @Schema(description = "여행 ID")
        Long tripId,

        @Schema(description = "장소 ID")
        Long placeId,

        @Schema(description = "장소명")
        String placeName,

        @Schema(description = "선택 여부")
        Boolean isSelected,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static TripPlaceResponse from(TripPlace tripPlace) {
        return new TripPlaceResponse(
                tripPlace.getId(),
                tripPlace.getTrip().getId(),
                tripPlace.getPlace().getId(),
                tripPlace.getPlace().getName(),
                tripPlace.getIsSelected(),
                tripPlace.getCreatedAt(),
                tripPlace.getModifiedAt()
        );
    }
}

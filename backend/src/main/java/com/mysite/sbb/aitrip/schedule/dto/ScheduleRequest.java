package com.mysite.sbb.aitrip.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(description = "일정 생성/수정 요청")
public record ScheduleRequest(
        @Schema(description = "장소 ID", example = "1")
        @NotNull(message = "장소 ID는 필수입니다.")
        Long placeId,

        @Schema(description = "일차", example = "1")
        @NotNull(message = "일차는 필수입니다.")
        Integer dayNumber,

        @Schema(description = "방문 순서", example = "1")
        @NotNull(message = "방문 순서는 필수입니다.")
        Integer visitOrder,

        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "11:30")
        LocalTime endTime,

        @Schema(description = "예상 대기시간(분)", example = "30")
        Integer estimatedWaitingTime,

        @Schema(description = "이전 장소로부터 이동시간(분)", example = "15")
        Integer travelTimeFromPrev,

        @Schema(description = "체류 시간(분)", example = "45")
        Integer stayDuration,

        @Schema(description = "메모")
        String notes
) {
}

package com.mysite.sbb.aitrip.schedule.dto;

import com.mysite.sbb.aitrip.schedule.domain.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Schema(description = "일정 응답")
public record ScheduleResponse(
        @Schema(description = "일정 ID", example = "1")
        Long id,

        @Schema(description = "여행 ID")
        Long tripId,

        @Schema(description = "장소 ID")
        Long placeId,

        @Schema(description = "장소명")
        String placeName,

        @Schema(description = "일차")
        Integer dayNumber,

        @Schema(description = "방문 순서")
        Integer visitOrder,

        @Schema(description = "시작 시간")
        LocalTime startTime,

        @Schema(description = "종료 시간")
        LocalTime endTime,

        @Schema(description = "예상 대기시간(분)")
        Integer estimatedWaitingTime,

        @Schema(description = "이동시간(분)")
        Integer travelTimeFromPrev,

        @Schema(description = "체류 시간(분)")
        Integer stayDuration,

        @Schema(description = "메모")
        String notes,

        @Schema(description = "생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "수정 시간")
        LocalDateTime modifiedAt
) {
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTrip().getId(),
                schedule.getPlace().getId(),
                schedule.getPlace().getName(),
                schedule.getDayNumber(),
                schedule.getVisitOrder(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getEstimatedWaitingTime(),
                schedule.getTravelTimeFromPrev(),
                schedule.getStayDuration(),
                schedule.getNotes(),
                schedule.getCreatedAt(),
                schedule.getModifiedAt()
        );
    }
}

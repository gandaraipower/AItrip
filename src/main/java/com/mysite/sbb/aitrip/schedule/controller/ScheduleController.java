package com.mysite.sbb.aitrip.schedule.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.security.CustomUserDetails;
import com.mysite.sbb.aitrip.schedule.dto.ScheduleRequest;
import com.mysite.sbb.aitrip.schedule.dto.ScheduleResponse;
import com.mysite.sbb.aitrip.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "일정 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 여행 일정 조회 API
    @GetMapping("/api/trips/{tripId}/schedules")
    @Operation(summary = "여행 일정 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(scheduleService.getSchedules(tripId, userDetails.getUserId())));
    }

    // 일정 생성 API
    @PostMapping("/api/trips/{tripId}/schedules")
    @Operation(summary = "일정 생성", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(scheduleService.createSchedule(tripId, userDetails.getUserId(), request)));
    }

    // 일정 수정 API
    @PutMapping("/api/schedules/{id}")
    @Operation(summary = "일정 수정", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(scheduleService.updateSchedule(id, userDetails.getUserId(), request)));
    }

    // 여행 일정 전체 삭제 API
    @DeleteMapping("/api/trips/{tripId}/schedules")
    @Operation(summary = "여행 일정 전체 삭제", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId) {
        scheduleService.deleteSchedulesByTripId(tripId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}

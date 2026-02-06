package com.mysite.sbb.aitrip.placemovingtime.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.placemovingtime.dto.PlaceMovingTimeRequest;
import com.mysite.sbb.aitrip.placemovingtime.dto.PlaceMovingTimeResponse;
import com.mysite.sbb.aitrip.placemovingtime.service.PlaceMovingTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PlaceMovingTime", description = "장소 이동시간 API")
public class PlaceMovingTimeController {

    private final PlaceMovingTimeService placeMovingTimeService;

    // 이동시간 조회 API
    @GetMapping("/api/place-moving-times")
    @Operation(summary = "이동시간 조회")
    public ResponseEntity<ApiResponse<List<PlaceMovingTimeResponse>>> getAllMovingTimes() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeMovingTimeService.getAllMovingTimes()));
    }

    // 이동시간 등록 API
    @PostMapping("/api/place-moving-times")
    @Operation(summary = "이동시간 등록", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PlaceMovingTimeResponse>> createMovingTime(
            @Valid @RequestBody PlaceMovingTimeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(placeMovingTimeService.createMovingTime(request)));
    }
}

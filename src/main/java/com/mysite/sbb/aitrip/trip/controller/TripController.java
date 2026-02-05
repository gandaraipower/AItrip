package com.mysite.sbb.aitrip.trip.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.security.CustomUserDetails;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.dto.TripRequest;
import com.mysite.sbb.aitrip.trip.dto.TripResponse;
import com.mysite.sbb.aitrip.trip.service.TripService;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Trip", description = "여행 API")
public class TripController {

    private final TripService tripService;

    // 여행 생성 API
    @PostMapping("/api/trips")
    @Operation(summary = "여행 생성", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tripService.createTrip(userDetails.getUserId(), request)));
    }

    // 내 여행 목록 조회 API
    @GetMapping("/api/trips")
    @Operation(summary = "내 여행 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<TripResponse>>> getMyTrips(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(tripService.getMyTrips(userDetails.getUserId())));
    }

    // 여행 상세 조회 API
    @GetMapping("/api/trips/{id}")
    @Operation(summary = "여행 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TripResponse>> getTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(tripService.getTrip(id, userDetails.getUserId())));
    }

    // 여행 수정 API
    @PutMapping("/api/trips/{id}")
    @Operation(summary = "여행 수정", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TripRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(tripService.updateTrip(id, userDetails.getUserId(), request)));
    }

    // 여행 삭제 API
    @DeleteMapping("/api/trips/{id}")
    @Operation(summary = "여행 삭제", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        tripService.deleteTrip(id, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    // 여행 상태 변경 API
    @PatchMapping("/api/trips/{id}/status")
    @Operation(summary = "여행 상태 변경", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TripResponse>> updateTripStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        TripStatus status = TripStatus.valueOf(body.get("status"));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(tripService.updateTripStatus(id, userDetails.getUserId(), status)));
    }
}

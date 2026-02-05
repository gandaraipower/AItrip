package com.mysite.sbb.aitrip.tripplace.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.security.CustomUserDetails;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceRequest;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceResponse;
import com.mysite.sbb.aitrip.tripplace.service.TripPlaceService;
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
@Tag(name = "TripPlace", description = "여행 장소 API")
public class TripPlaceController {

    private final TripPlaceService tripPlaceService;

    // 여행 장소 목록 조회 API
    @GetMapping("/api/trips/{tripId}/places")
    @Operation(summary = "여행 장소 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<TripPlaceResponse>>> getTripPlaces(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(tripPlaceService.getTripPlaces(tripId, userDetails.getUserId())));
    }

    // 여행에 장소 추가 API
    @PostMapping("/api/trips/{tripId}/places")
    @Operation(summary = "여행에 장소 추가", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TripPlaceResponse>> addPlaceToTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody TripPlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tripPlaceService.addPlaceToTrip(tripId, userDetails.getUserId(), request)));
    }

    // 여행에서 장소 제거 API
    @DeleteMapping("/api/trips/{tripId}/places/{placeId}")
    @Operation(summary = "여행에서 장소 제거", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> removePlaceFromTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @PathVariable Long placeId) {
        tripPlaceService.removePlaceFromTrip(tripId, userDetails.getUserId(), placeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}

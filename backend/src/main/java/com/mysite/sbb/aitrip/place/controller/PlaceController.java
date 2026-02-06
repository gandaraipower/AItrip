package com.mysite.sbb.aitrip.place.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.place.dto.PlaceRequest;
import com.mysite.sbb.aitrip.place.dto.PlaceResponse;
import com.mysite.sbb.aitrip.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Place", description = "장소 API")
public class PlaceController {

    private final PlaceService placeService;

    // 장소 목록 조회 API
    @GetMapping("/api/places")
    @Operation(summary = "장소 목록 조회")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getAllPlaces() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeService.getAllPlaces()));
    }

    // 장소 상세 조회 API
    @GetMapping("/api/places/{id}")
    @Operation(summary = "장소 상세 조회")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlace(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeService.getPlace(id)));
    }

    // 장소 등록 API (관리자)
    @PostMapping("/api/places")
    @Operation(summary = "장소 등록 (관리자)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PlaceResponse>> createPlace(@Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(placeService.createPlace(request)));
    }

    // 장소 수정 API (관리자)
    @PutMapping("/api/places/{id}")
    @Operation(summary = "장소 수정 (관리자)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PlaceResponse>> updatePlace(
            @PathVariable Long id, @Valid @RequestBody PlaceRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeService.updatePlace(id, request)));
    }

    // 장소 삭제 API (관리자)
    @DeleteMapping("/api/places/{id}")
    @Operation(summary = "장소 삭제 (관리자)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}

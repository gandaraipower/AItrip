package com.mysite.sbb.aitrip.placestyletag.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.placestyletag.dto.PlaceStyleTagRequest;
import com.mysite.sbb.aitrip.placestyletag.dto.PlaceStyleTagResponse;
import com.mysite.sbb.aitrip.placestyletag.service.PlaceStyleTagService;
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
@Tag(name = "PlaceStyleTag", description = "장소 스타일 태그 API")
public class PlaceStyleTagController {

    private final PlaceStyleTagService placeStyleTagService;

    // 장소 태그 조회 API
    @GetMapping("/api/places/{placeId}/tags")
    @Operation(summary = "장소 스타일 태그 조회")
    public ResponseEntity<ApiResponse<List<PlaceStyleTagResponse>>> getTags(@PathVariable Long placeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeStyleTagService.getTagsByPlaceId(placeId)));
    }

    // 장소 태그 등록 API
    @PostMapping("/api/places/{placeId}/tags")
    @Operation(summary = "장소 스타일 태그 등록", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PlaceStyleTagResponse>> createTag(
            @PathVariable Long placeId, @Valid @RequestBody PlaceStyleTagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(placeStyleTagService.createTag(placeId, request)));
    }
}

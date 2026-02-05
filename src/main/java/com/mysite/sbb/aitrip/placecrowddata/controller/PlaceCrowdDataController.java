package com.mysite.sbb.aitrip.placecrowddata.controller;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.placecrowddata.dto.PlaceCrowdDataRequest;
import com.mysite.sbb.aitrip.placecrowddata.dto.PlaceCrowdDataResponse;
import com.mysite.sbb.aitrip.placecrowddata.service.PlaceCrowdDataService;
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
@Tag(name = "PlaceCrowdData", description = "장소 혼잡도 API")
public class PlaceCrowdDataController {

    private final PlaceCrowdDataService placeCrowdDataService;

    // 혼잡도 조회 API
    @GetMapping("/api/place-crowd-data")
    @Operation(summary = "혼잡도 조회")
    public ResponseEntity<ApiResponse<List<PlaceCrowdDataResponse>>> getAllCrowdData() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(placeCrowdDataService.getAllCrowdData()));
    }

    // 혼잡도 등록 API
    @PostMapping("/api/place-crowd-data")
    @Operation(summary = "혼잡도 등록", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PlaceCrowdDataResponse>> createCrowdData(
            @Valid @RequestBody PlaceCrowdDataRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(placeCrowdDataService.createCrowdData(request)));
    }
}

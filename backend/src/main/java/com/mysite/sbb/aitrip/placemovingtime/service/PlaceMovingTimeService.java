package com.mysite.sbb.aitrip.placemovingtime.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.placemovingtime.domain.PlaceMovingTime;
import com.mysite.sbb.aitrip.placemovingtime.dto.PlaceMovingTimeRequest;
import com.mysite.sbb.aitrip.placemovingtime.dto.PlaceMovingTimeResponse;
import com.mysite.sbb.aitrip.placemovingtime.repository.PlaceMovingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceMovingTimeService {

    private final PlaceMovingTimeRepository placeMovingTimeRepository;
    private final PlaceRepository placeRepository;

    // 이동시간 목록 조회
    public List<PlaceMovingTimeResponse> getAllMovingTimes() {
        return placeMovingTimeRepository.findAll().stream()
                .map(PlaceMovingTimeResponse::from)
                .toList();
    }

    // 이동시간 등록
    @Transactional
    public PlaceMovingTimeResponse createMovingTime(PlaceMovingTimeRequest request) {
        Place fromPlace = placeRepository.findById(request.fromPlaceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
        Place toPlace = placeRepository.findById(request.toPlaceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

        PlaceMovingTime movingTime = PlaceMovingTime.builder()
                .fromPlace(fromPlace)
                .toPlace(toPlace)
                .distanceKm(request.distanceKm())
                .timeMinutes(request.timeMinutes())
                .transportType(request.transportType())
                .build();

        PlaceMovingTime saved = placeMovingTimeRepository.save(movingTime);
        return PlaceMovingTimeResponse.from(saved);
    }
}

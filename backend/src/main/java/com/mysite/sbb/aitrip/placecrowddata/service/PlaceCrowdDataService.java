package com.mysite.sbb.aitrip.placecrowddata.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.placecrowddata.domain.PlaceCrowdData;
import com.mysite.sbb.aitrip.placecrowddata.dto.PlaceCrowdDataRequest;
import com.mysite.sbb.aitrip.placecrowddata.dto.PlaceCrowdDataResponse;
import com.mysite.sbb.aitrip.placecrowddata.repository.PlaceCrowdDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceCrowdDataService {

    private final PlaceCrowdDataRepository placeCrowdDataRepository;
    private final PlaceRepository placeRepository;

    // 혼잡도 목록 조회
    public List<PlaceCrowdDataResponse> getAllCrowdData() {
        return placeCrowdDataRepository.findAll().stream()
                .map(PlaceCrowdDataResponse::from)
                .toList();
    }

    // 혼잡도 등록
    @Transactional
    public PlaceCrowdDataResponse createCrowdData(PlaceCrowdDataRequest request) {
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

        PlaceCrowdData crowdData = PlaceCrowdData.builder()
                .place(place)
                .dayOfWeek(request.dayOfWeek())
                .hour(request.hour())
                .crowdLevel(request.crowdLevel())
                .waitingRiskScore(request.waitingRiskScore())
                .avgWaitingMin(request.avgWaitingMin())
                .build();

        PlaceCrowdData saved = placeCrowdDataRepository.save(crowdData);
        return PlaceCrowdDataResponse.from(saved);
    }
}

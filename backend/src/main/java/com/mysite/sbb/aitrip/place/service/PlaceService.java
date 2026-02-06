package com.mysite.sbb.aitrip.place.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.dto.PlaceRequest;
import com.mysite.sbb.aitrip.place.dto.PlaceResponse;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    // 장소 목록 조회
    public List<PlaceResponse> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(PlaceResponse::from)
                .toList();
    }

    // 장소 상세 조회
    public PlaceResponse getPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
        return PlaceResponse.from(place);
    }

    // 장소 등록 (관리자)
    @Transactional
    public PlaceResponse createPlace(PlaceRequest request) {
        Place place = request.toEntity();
        Place savedPlace = placeRepository.save(place);
        return PlaceResponse.from(savedPlace);
    }

    // 장소 수정 (관리자)
    @Transactional
    public PlaceResponse updatePlace(Long id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
        place.update(request.name(), request.region(), request.category(), request.address(),
                request.latitude(), request.longitude(), request.operatingHours(),
                request.estimatedStayTime(), request.imageUrl(), request.source());
        return PlaceResponse.from(place);
    }

    // 장소 삭제 (관리자)
    @Transactional
    public void deletePlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
        placeRepository.delete(place);
    }
}

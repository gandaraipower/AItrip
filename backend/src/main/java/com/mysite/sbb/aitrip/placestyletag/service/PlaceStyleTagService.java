package com.mysite.sbb.aitrip.placestyletag.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.placestyletag.domain.PlaceStyleTag;
import com.mysite.sbb.aitrip.placestyletag.dto.PlaceStyleTagRequest;
import com.mysite.sbb.aitrip.placestyletag.dto.PlaceStyleTagResponse;
import com.mysite.sbb.aitrip.placestyletag.repository.PlaceStyleTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceStyleTagService {

    private final PlaceStyleTagRepository placeStyleTagRepository;
    private final PlaceRepository placeRepository;

    // 장소의 스타일 태그 조회
    public List<PlaceStyleTagResponse> getTagsByPlaceId(Long placeId) {
        return placeStyleTagRepository.findByPlaceId(placeId).stream()
                .map(PlaceStyleTagResponse::from)
                .toList();
    }

    // 장소에 스타일 태그 등록
    @Transactional
    public PlaceStyleTagResponse createTag(Long placeId, PlaceStyleTagRequest request) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
        PlaceStyleTag tag = request.toEntity(place);
        PlaceStyleTag savedTag = placeStyleTagRepository.save(tag);
        return PlaceStyleTagResponse.from(savedTag);
    }
}

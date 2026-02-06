package com.mysite.sbb.aitrip.tripplace.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import com.mysite.sbb.aitrip.tripplace.domain.TripPlace;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceRequest;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceResponse;
import com.mysite.sbb.aitrip.tripplace.repository.TripPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripPlaceService {

    private final TripPlaceRepository tripPlaceRepository;
    private final TripRepository tripRepository;
    private final PlaceRepository placeRepository;

    // 여행 장소 목록 조회
    public List<TripPlaceResponse> getTripPlaces(Long tripId, Long userId) {
        validateTripOwner(tripId, userId);
        return tripPlaceRepository.findByTripId(tripId).stream()
                .map(TripPlaceResponse::from)
                .toList();
    }

    // 여행에 장소 추가
    @Transactional
    public TripPlaceResponse addPlaceToTrip(Long tripId, Long userId, TripPlaceRequest request) {
        Trip trip = validateTripOwner(tripId, userId);

        if (tripPlaceRepository.existsByTripIdAndPlaceId(tripId, request.placeId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_TRIP_PLACE);
        }

        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

        TripPlace tripPlace = TripPlace.builder()
                .trip(trip)
                .place(place)
                .isSelected(request.isSelected() != null ? request.isSelected() : true)
                .build();

        TripPlace saved = tripPlaceRepository.save(tripPlace);
        return TripPlaceResponse.from(saved);
    }

    // 여행에서 장소 제거
    @Transactional
    public void removePlaceFromTrip(Long tripId, Long userId, Long placeId) {
        validateTripOwner(tripId, userId);
        TripPlace tripPlace = tripPlaceRepository.findByTripIdAndPlaceId(tripId, placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TRIP_PLACE));
        tripPlaceRepository.delete(tripPlace);
    }

    private Trip validateTripOwner(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TRIP));
        if (!trip.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TRIP_ACCESS);
        }
        return trip;
    }
}

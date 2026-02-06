package com.mysite.sbb.aitrip.trip.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.dto.TripRequest;
import com.mysite.sbb.aitrip.trip.dto.TripResponse;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    // 여행 생성
    @Transactional
    public TripResponse createTrip(Long userId, TripRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Trip trip = request.toEntity(user);
        Trip savedTrip = tripRepository.save(trip);
        return TripResponse.from(savedTrip);
    }

    // 내 여행 목록 조회
    public List<TripResponse> getMyTrips(Long userId) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(TripResponse::from)
                .toList();
    }

    // 여행 상세 조회
    public TripResponse getTrip(Long tripId, Long userId) {
        Trip trip = findTripByIdAndValidateOwner(tripId, userId);
        return TripResponse.from(trip);
    }

    // 여행 수정
    @Transactional
    public TripResponse updateTrip(Long tripId, Long userId, TripRequest request) {
        Trip trip = findTripByIdAndValidateOwner(tripId, userId);
        trip.update(request.title(), request.region(), request.style(),
                request.tripStyle(), request.startDate(), request.endDate());
        return TripResponse.from(trip);
    }

    // 여행 삭제
    @Transactional
    public void deleteTrip(Long tripId, Long userId) {
        Trip trip = findTripByIdAndValidateOwner(tripId, userId);
        tripRepository.delete(trip);
    }

    // 여행 상태 변경
    @Transactional
    public TripResponse updateTripStatus(Long tripId, Long userId, TripStatus status) {
        Trip trip = findTripByIdAndValidateOwner(tripId, userId);
        trip.updateStatus(status);
        return TripResponse.from(trip);
    }

    private Trip findTripByIdAndValidateOwner(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TRIP));
        if (!trip.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TRIP_ACCESS);
        }
        return trip;
    }
}

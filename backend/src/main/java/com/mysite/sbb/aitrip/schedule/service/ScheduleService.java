package com.mysite.sbb.aitrip.schedule.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.schedule.domain.Schedule;
import com.mysite.sbb.aitrip.schedule.dto.ScheduleRequest;
import com.mysite.sbb.aitrip.schedule.dto.ScheduleResponse;
import com.mysite.sbb.aitrip.schedule.repository.ScheduleRepository;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;
    private final PlaceRepository placeRepository;

    // 여행 일정 조회
    public List<ScheduleResponse> getSchedules(Long tripId, Long userId) {
        validateTripOwner(tripId, userId);
        return scheduleRepository.findByTripIdOrderByDayNumberAscVisitOrderAsc(tripId).stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    // 일정 생성
    @Transactional
    public ScheduleResponse createSchedule(Long tripId, Long userId, ScheduleRequest request) {
        Trip trip = validateTripOwner(tripId, userId);
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

        Schedule schedule = Schedule.builder()
                .trip(trip)
                .place(place)
                .dayNumber(request.dayNumber())
                .visitOrder(request.visitOrder())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .estimatedWaitingTime(request.estimatedWaitingTime())
                .travelTimeFromPrev(request.travelTimeFromPrev())
                .stayDuration(request.stayDuration())
                .notes(request.notes())
                .build();

        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleResponse.from(saved);
    }

    // 일정 수정
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, Long userId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_SCHEDULE));

        if (!schedule.getTrip().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TRIP_ACCESS);
        }

        schedule.update(request.dayNumber(), request.visitOrder(), request.startTime(),
                request.endTime(), request.estimatedWaitingTime(), request.travelTimeFromPrev(),
                request.stayDuration(), request.notes());

        return ScheduleResponse.from(schedule);
    }

    // 여행 일정 전체 삭제
    @Transactional
    public void deleteSchedulesByTripId(Long tripId, Long userId) {
        validateTripOwner(tripId, userId);
        scheduleRepository.deleteByTripId(tripId);
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

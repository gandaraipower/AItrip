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
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleService 테스트")
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("일정 생성 - 성공")
    void createSchedule_success() {
        // given
        User user = createUser(1L);
        Trip trip = createTrip(1L, user);
        Place place = createPlace(1L, "블루보틀");

        given(tripRepository.findById(1L)).willReturn(Optional.of(trip));
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));

        Schedule saved = Schedule.builder()
                .trip(trip).place(place).dayNumber(1).visitOrder(1)
                .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 0))
                .stayDuration(45).build();
        setId(saved, Schedule.class, 1L);

        given(scheduleRepository.save(any(Schedule.class))).willReturn(saved);

        ScheduleRequest request = new ScheduleRequest(1L, 1, 1,
                LocalTime.of(10, 0), LocalTime.of(11, 0), 30, 15, 45, null);

        // when
        ScheduleResponse result = scheduleService.createSchedule(1L, 1L, request);

        // then
        assertThat(result.dayNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("일정 조회 - 여행 없음")
    void getSchedules_tripNotFound() {
        // given
        given(tripRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleService.getSchedules(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_TRIP));
    }

    private User createUser(Long id) {
        User user = User.builder().email("test@example.com").password("pw").name("홍길동").role(User.Role.ROLE_USER).build();
        setId(user, User.class, id);
        return user;
    }

    private Trip createTrip(Long id, User user) {
        Trip trip = Trip.builder().user(user).title("봄 여행").region("서울").tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10)).endDate(LocalDate.of(2025, 3, 13)).status(TripStatus.DRAFT).build();
        setId(trip, Trip.class, id);
        return trip;
    }

    private Place createPlace(Long id, String name) {
        Place place = Place.builder().name(name).region("서울").category("카페").address("강남구").build();
        setId(place, Place.class, id);
        return place;
    }

    private <T> void setId(T entity, Class<T> clazz, Long id) {
        try {
            java.lang.reflect.Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

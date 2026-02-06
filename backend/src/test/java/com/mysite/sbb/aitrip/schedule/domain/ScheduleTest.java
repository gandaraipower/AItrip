package com.mysite.sbb.aitrip.schedule.domain;

import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Schedule 엔티티 테스트")
class ScheduleTest {

    @Test
    @DisplayName("Schedule 엔티티 생성")
    void createSchedule() {
        // given
        User user = User.builder().email("test@example.com").password("pw").name("홍길동").role(User.Role.ROLE_USER).build();
        Trip trip = Trip.builder().user(user).title("봄 여행").region("서울").tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10)).endDate(LocalDate.of(2025, 3, 13)).status(TripStatus.DRAFT).build();
        Place place = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();

        // when
        Schedule schedule = Schedule.builder()
                .trip(trip)
                .place(place)
                .dayNumber(1)
                .visitOrder(1)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 30))
                .estimatedWaitingTime(30)
                .travelTimeFromPrev(15)
                .stayDuration(45)
                .notes("첫 번째 방문")
                .build();

        // then
        assertThat(schedule.getDayNumber()).isEqualTo(1);
        assertThat(schedule.getVisitOrder()).isEqualTo(1);
        assertThat(schedule.getStayDuration()).isEqualTo(45);
    }

    @Test
    @DisplayName("Schedule 수정")
    void updateSchedule() {
        // given
        User user = User.builder().email("test@example.com").password("pw").name("홍길동").role(User.Role.ROLE_USER).build();
        Trip trip = Trip.builder().user(user).title("봄 여행").region("서울").tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10)).endDate(LocalDate.of(2025, 3, 13)).status(TripStatus.DRAFT).build();
        Place place = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();

        Schedule schedule = Schedule.builder()
                .trip(trip).place(place).dayNumber(1).visitOrder(1)
                .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 30))
                .stayDuration(45).build();

        // when
        schedule.update(2, 3, LocalTime.of(14, 0), LocalTime.of(15, 30), 20, 10, 60, "수정된 메모");

        // then
        assertThat(schedule.getDayNumber()).isEqualTo(2);
        assertThat(schedule.getVisitOrder()).isEqualTo(3);
        assertThat(schedule.getStayDuration()).isEqualTo(60);
    }
}

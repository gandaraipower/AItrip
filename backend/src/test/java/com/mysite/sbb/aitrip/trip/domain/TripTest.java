package com.mysite.sbb.aitrip.trip.domain;

import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Trip 엔티티 테스트")
class TripTest {

    @Test
    @DisplayName("Trip 엔티티 생성 - Builder 패턴")
    void createTrip() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build();

        // when
        Trip trip = Trip.builder()
                .user(user)
                .title("봄 여행")
                .region("서울")
                .style("감성적인 카페")
                .tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10))
                .endDate(LocalDate.of(2025, 3, 13))
                .status(TripStatus.DRAFT)
                .build();

        // then
        assertThat(trip.getTitle()).isEqualTo("봄 여행");
        assertThat(trip.getRegion()).isEqualTo("서울");
        assertThat(trip.getTripStyle()).isEqualTo(TripStyle.NORMAL);
        assertThat(trip.getStatus()).isEqualTo(TripStatus.DRAFT);
    }

    @Test
    @DisplayName("Trip 상태 변경")
    void updateStatus() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build();

        Trip trip = Trip.builder()
                .user(user)
                .title("봄 여행")
                .region("서울")
                .style("감성적인 카페")
                .tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10))
                .endDate(LocalDate.of(2025, 3, 13))
                .status(TripStatus.DRAFT)
                .build();

        // when
        trip.updateStatus(TripStatus.SCHEDULED);

        // then
        assertThat(trip.getStatus()).isEqualTo(TripStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Trip 정보 수정")
    void updateTrip() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build();

        Trip trip = Trip.builder()
                .user(user)
                .title("봄 여행")
                .region("서울")
                .style("감성적인 카페")
                .tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10))
                .endDate(LocalDate.of(2025, 3, 13))
                .status(TripStatus.DRAFT)
                .build();

        // when
        trip.update("여름 여행", "부산", "해변 위주", TripStyle.TIGHT,
                LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 5));

        // then
        assertThat(trip.getTitle()).isEqualTo("여름 여행");
        assertThat(trip.getRegion()).isEqualTo("부산");
        assertThat(trip.getTripStyle()).isEqualTo(TripStyle.TIGHT);
    }
}

package com.mysite.sbb.aitrip.tripplace.domain;

import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TripPlace 엔티티 테스트")
class TripPlaceTest {

    @Test
    @DisplayName("TripPlace 엔티티 생성")
    void createTripPlace() {
        // given
        User user = User.builder().email("test@example.com").password("pw").name("홍길동").role(User.Role.ROLE_USER).build();
        Trip trip = Trip.builder().user(user).title("봄 여행").region("서울").tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10)).endDate(LocalDate.of(2025, 3, 13)).status(TripStatus.DRAFT).build();
        Place place = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();

        // when
        TripPlace tripPlace = TripPlace.builder()
                .trip(trip)
                .place(place)
                .isSelected(true)
                .build();

        // then
        assertThat(tripPlace.getTrip()).isEqualTo(trip);
        assertThat(tripPlace.getPlace()).isEqualTo(place);
        assertThat(tripPlace.getIsSelected()).isTrue();
    }
}

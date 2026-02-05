package com.mysite.sbb.aitrip.placemovingtime.domain;

import com.mysite.sbb.aitrip.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlaceMovingTime 엔티티 테스트")
class PlaceMovingTimeTest {

    @Test
    @DisplayName("PlaceMovingTime 엔티티 생성")
    void createPlaceMovingTime() {
        // given
        Place from = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();
        Place to = Place.builder().name("남산타워").region("서울").category("관광지").address("용산구").build();

        // when
        PlaceMovingTime movingTime = PlaceMovingTime.builder()
                .fromPlace(from)
                .toPlace(to)
                .distanceKm(new BigDecimal("3.5"))
                .timeMinutes(15)
                .transportType("도보")
                .build();

        // then
        assertThat(movingTime.getDistanceKm()).isEqualByComparingTo(new BigDecimal("3.5"));
        assertThat(movingTime.getTimeMinutes()).isEqualTo(15);
        assertThat(movingTime.getTransportType()).isEqualTo("도보");
    }
}

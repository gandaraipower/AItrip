package com.mysite.sbb.aitrip.place.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Place 엔티티 테스트")
class PlaceTest {

    @Test
    @DisplayName("Place 엔티티 생성 - Builder 패턴")
    void createPlace() {
        // given & when
        Place place = Place.builder()
                .name("블루보틀")
                .region("서울")
                .category("카페")
                .address("강남구 압구정로 27")
                .latitude(new BigDecimal("37.5265"))
                .longitude(new BigDecimal("127.0402"))
                .operatingHours("09:00-21:00")
                .estimatedStayTime(45)
                .source("TourAPI")
                .build();

        // then
        assertThat(place.getName()).isEqualTo("블루보틀");
        assertThat(place.getRegion()).isEqualTo("서울");
        assertThat(place.getCategory()).isEqualTo("카페");
        assertThat(place.getEstimatedStayTime()).isEqualTo(45);
    }

    @Test
    @DisplayName("Place 정보 수정")
    void updatePlace() {
        // given
        Place place = Place.builder()
                .name("블루보틀")
                .region("서울")
                .category("카페")
                .address("강남구 압구정로 27")
                .build();

        // when
        place.update("스타벅스", "부산", "카페", "해운대구 해운대해변로 264",
                new BigDecimal("35.1587"), new BigDecimal("129.1604"),
                "08:00-22:00", 30, null, "Kakao");

        // then
        assertThat(place.getName()).isEqualTo("스타벅스");
        assertThat(place.getRegion()).isEqualTo("부산");
    }
}

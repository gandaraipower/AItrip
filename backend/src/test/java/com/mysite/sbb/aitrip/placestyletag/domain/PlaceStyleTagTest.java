package com.mysite.sbb.aitrip.placestyletag.domain;

import com.mysite.sbb.aitrip.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlaceStyleTag 엔티티 테스트")
class PlaceStyleTagTest {

    @Test
    @DisplayName("PlaceStyleTag 엔티티 생성")
    void createPlaceStyleTag() {
        // given
        Place place = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();

        // when
        PlaceStyleTag tag = PlaceStyleTag.builder()
                .place(place)
                .tag("감성")
                .frequency(15)
                .weight(new BigDecimal("0.85"))
                .build();

        // then
        assertThat(tag.getTag()).isEqualTo("감성");
        assertThat(tag.getFrequency()).isEqualTo(15);
        assertThat(tag.getWeight()).isEqualByComparingTo(new BigDecimal("0.85"));
    }
}

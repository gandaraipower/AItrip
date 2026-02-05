package com.mysite.sbb.aitrip.placecrowddata.domain;

import com.mysite.sbb.aitrip.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlaceCrowdData 엔티티 테스트")
class PlaceCrowdDataTest {

    @Test
    @DisplayName("PlaceCrowdData 엔티티 생성")
    void createPlaceCrowdData() {
        // given
        Place place = Place.builder().name("블루보틀").region("서울").category("카페").address("강남구").build();

        // when
        PlaceCrowdData crowdData = PlaceCrowdData.builder()
                .place(place)
                .dayOfWeek(1)
                .hour(12)
                .crowdLevel("높음")
                .waitingRiskScore(new BigDecimal("0.75"))
                .avgWaitingMin(30)
                .build();

        // then
        assertThat(crowdData.getDayOfWeek()).isEqualTo(1);
        assertThat(crowdData.getHour()).isEqualTo(12);
        assertThat(crowdData.getCrowdLevel()).isEqualTo("높음");
        assertThat(crowdData.getAvgWaitingMin()).isEqualTo(30);
    }
}

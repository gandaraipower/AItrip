package com.mysite.sbb.aitrip.place.repository;

import com.mysite.sbb.aitrip.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("PlaceRepository 테스트")
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("장소 저장")
    void save() {
        // given
        Place place = Place.builder()
                .name("블루보틀")
                .region("서울")
                .category("카페")
                .address("강남구 압구정로 27")
                .build();

        // when
        Place saved = placeRepository.save(place);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("블루보틀");
    }

    @Test
    @DisplayName("지역으로 장소 조회")
    void findByRegion() {
        // given
        placeRepository.save(Place.builder()
                .name("블루보틀").region("서울").category("카페").address("강남구").build());
        placeRepository.save(Place.builder()
                .name("남산타워").region("서울").category("관광지").address("용산구").build());
        placeRepository.save(Place.builder()
                .name("해운대").region("부산").category("관광지").address("해운대구").build());

        // when
        List<Place> seoulPlaces = placeRepository.findByRegion("서울");

        // then
        assertThat(seoulPlaces).hasSize(2);
    }

    @Test
    @DisplayName("장소 삭제")
    void delete() {
        // given
        Place place = placeRepository.save(Place.builder()
                .name("블루보틀").region("서울").category("카페").address("강남구").build());
        Long id = place.getId();

        // when
        placeRepository.delete(place);

        // then
        assertThat(placeRepository.findById(id)).isEmpty();
    }
}

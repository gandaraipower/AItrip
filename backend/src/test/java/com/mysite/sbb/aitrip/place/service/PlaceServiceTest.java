package com.mysite.sbb.aitrip.place.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.dto.PlaceRequest;
import com.mysite.sbb.aitrip.place.dto.PlaceResponse;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceService 테스트")
class PlaceServiceTest {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("장소 목록 조회")
    void getAllPlaces() {
        // given
        List<Place> places = List.of(
                createPlace(1L, "블루보틀"),
                createPlace(2L, "남산타워")
        );
        given(placeRepository.findAll()).willReturn(places);

        // when
        List<PlaceResponse> result = placeService.getAllPlaces();

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("장소 상세 조회 - 존재하지 않는 장소")
    void getPlace_notFound() {
        // given
        given(placeRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeService.getPlace(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_PLACE);
                });
    }

    @Test
    @DisplayName("장소 등록")
    void createPlace() {
        // given
        PlaceRequest request = new PlaceRequest("블루보틀", "서울", "카페",
                "강남구 압구정로 27", new BigDecimal("37.5265"), new BigDecimal("127.0402"),
                "09:00-21:00", 45, null, "TourAPI");

        Place savedPlace = createPlace(1L, "블루보틀");
        given(placeRepository.save(any(Place.class))).willReturn(savedPlace);

        // when
        PlaceResponse result = placeService.createPlace(request);

        // then
        assertThat(result.name()).isEqualTo("블루보틀");
        verify(placeRepository).save(any(Place.class));
    }

    private Place createPlace(Long id, String name) {
        Place place = Place.builder()
                .name(name)
                .region("서울")
                .category("카페")
                .address("강남구")
                .build();
        try {
            java.lang.reflect.Field idField = Place.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(place, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return place;
    }
}

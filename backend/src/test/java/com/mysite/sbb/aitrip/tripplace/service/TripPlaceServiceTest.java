package com.mysite.sbb.aitrip.tripplace.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import com.mysite.sbb.aitrip.tripplace.domain.TripPlace;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceRequest;
import com.mysite.sbb.aitrip.tripplace.dto.TripPlaceResponse;
import com.mysite.sbb.aitrip.tripplace.repository.TripPlaceRepository;
import com.mysite.sbb.aitrip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripPlaceService 테스트")
class TripPlaceServiceTest {

    @InjectMocks
    private TripPlaceService tripPlaceService;

    @Mock
    private TripPlaceRepository tripPlaceRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("여행에 장소 추가 - 성공")
    void addPlaceToTrip_success() {
        // given
        User user = createUser(1L);
        Trip trip = createTrip(1L, user);
        Place place = createPlace(1L, "블루보틀");

        given(tripRepository.findById(1L)).willReturn(Optional.of(trip));
        given(tripPlaceRepository.existsByTripIdAndPlaceId(1L, 1L)).willReturn(false);
        given(placeRepository.findById(1L)).willReturn(Optional.of(place));

        TripPlace saved = TripPlace.builder().trip(trip).place(place).isSelected(true).build();
        setId(saved, TripPlace.class, 1L);
        given(tripPlaceRepository.save(any(TripPlace.class))).willReturn(saved);

        TripPlaceRequest request = new TripPlaceRequest(1L, true);

        // when
        TripPlaceResponse result = tripPlaceService.addPlaceToTrip(1L, 1L, request);

        // then
        assertThat(result.placeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("여행에 장소 추가 - 중복")
    void addPlaceToTrip_duplicate() {
        // given
        User user = createUser(1L);
        Trip trip = createTrip(1L, user);

        given(tripRepository.findById(1L)).willReturn(Optional.of(trip));
        given(tripPlaceRepository.existsByTripIdAndPlaceId(1L, 1L)).willReturn(true);

        TripPlaceRequest request = new TripPlaceRequest(1L, true);

        // when & then
        assertThatThrownBy(() -> tripPlaceService.addPlaceToTrip(1L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_TRIP_PLACE));
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

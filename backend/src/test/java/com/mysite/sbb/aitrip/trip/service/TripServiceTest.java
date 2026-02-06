package com.mysite.sbb.aitrip.trip.service;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.trip.dto.TripRequest;
import com.mysite.sbb.aitrip.trip.dto.TripResponse;
import com.mysite.sbb.aitrip.trip.repository.TripRepository;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripService 테스트")
class TripServiceTest {

    @InjectMocks
    private TripService tripService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("여행 생성 - 성공")
    void createTrip_success() {
        // given
        User user = createUser(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        TripRequest request = new TripRequest("봄 여행", "서울", "감성 카페",
                TripStyle.NORMAL, LocalDate.of(2025, 3, 10), LocalDate.of(2025, 3, 13));

        Trip savedTrip = createTrip(1L, user, "봄 여행");
        given(tripRepository.save(any(Trip.class))).willReturn(savedTrip);

        // when
        TripResponse result = tripService.createTrip(1L, request);

        // then
        assertThat(result.title()).isEqualTo("봄 여행");
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    @DisplayName("여행 조회 - 존재하지 않는 여행")
    void getTrip_notFound() {
        // given
        given(tripRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripService.getTrip(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_TRIP);
                });
    }

    @Test
    @DisplayName("여행 조회 - 권한 없음")
    void getTrip_unauthorized() {
        // given
        User owner = createUser(1L);
        Trip trip = createTrip(1L, owner, "봄 여행");
        given(tripRepository.findById(1L)).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> tripService.getTrip(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_TRIP_ACCESS);
                });
    }

    @Test
    @DisplayName("내 여행 목록 조회")
    void getMyTrips() {
        // given
        User user = createUser(1L);
        List<Trip> trips = List.of(
                createTrip(1L, user, "여행1"),
                createTrip(2L, user, "여행2")
        );
        given(tripRepository.findByUserIdOrderByCreatedAtDesc(1L)).willReturn(trips);

        // when
        List<TripResponse> result = tripService.getMyTrips(1L);

        // then
        assertThat(result).hasSize(2);
    }

    private User createUser(Long id) {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build();
        setId(user, User.class, id);
        return user;
    }

    private Trip createTrip(Long id, User user, String title) {
        Trip trip = Trip.builder()
                .user(user)
                .title(title)
                .region("서울")
                .style("감성 카페")
                .tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10))
                .endDate(LocalDate.of(2025, 3, 13))
                .status(TripStatus.DRAFT)
                .build();
        setId(trip, Trip.class, id);
        return trip;
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

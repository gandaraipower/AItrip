package com.mysite.sbb.aitrip.trip.repository;

import com.mysite.sbb.aitrip.trip.domain.Trip;
import com.mysite.sbb.aitrip.trip.domain.TripStatus;
import com.mysite.sbb.aitrip.trip.domain.TripStyle;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("TripRepository 테스트")
class TripRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("여행 저장")
    void save() {
        // given
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build());

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
        Trip savedTrip = tripRepository.save(trip);

        // then
        assertThat(savedTrip.getId()).isNotNull();
        assertThat(savedTrip.getTitle()).isEqualTo("봄 여행");
    }

    @Test
    @DisplayName("사용자 ID로 여행 목록 조회")
    void findByUserId() {
        // given
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .name("홍길동")
                .role(User.Role.ROLE_USER)
                .build());

        tripRepository.save(Trip.builder()
                .user(user).title("여행1").region("서울").tripStyle(TripStyle.NORMAL)
                .startDate(LocalDate.of(2025, 3, 10)).endDate(LocalDate.of(2025, 3, 13))
                .status(TripStatus.DRAFT).build());

        tripRepository.save(Trip.builder()
                .user(user).title("여행2").region("부산").tripStyle(TripStyle.TIGHT)
                .startDate(LocalDate.of(2025, 7, 1)).endDate(LocalDate.of(2025, 7, 5))
                .status(TripStatus.DRAFT).build());

        // when
        List<Trip> trips = tripRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        // then
        assertThat(trips).hasSize(2);
    }
}

package com.mysite.sbb.aitrip.global.init;

import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.place.repository.PlaceRepository;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 개발 환경 초기 데이터 생성
 * dev 프로파일에서만 실행됩니다.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class BaseInitData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("=== 개발용 Mock 데이터 초기화 시작 ===");

        createUsers();
        createSeoulPlaces();
        createBusanPlaces();
        createJejuPlaces();

        log.info("=== 개발용 Mock 데이터 초기화 완료 ===");
    }

    private void createUsers() {
        String encodedPassword = passwordEncoder.encode("Test1234!");

        userRepository.save(User.builder()
                .email("admin@aitrip.com")
                .password(encodedPassword)
                .name("관리자")
                .role(User.Role.ROLE_ADMIN)
                .build());

        userRepository.save(User.builder()
                .email("user1@test.com")
                .password(encodedPassword)
                .name("테스트유저1")
                .role(User.Role.ROLE_USER)
                .build());

        userRepository.save(User.builder()
                .email("user2@test.com")
                .password(encodedPassword)
                .name("테스트유저2")
                .role(User.Role.ROLE_USER)
                .build());

        log.info("테스트 계정 3개 생성 완료");
    }

    private void createSeoulPlaces() {
        placeRepository.save(Place.builder()
                .name("경복궁")
                .region("서울")
                .category("관광명소")
                .address("서울특별시 종로구 사직로 161")
                .latitude(new BigDecimal("37.5796212"))
                .longitude(new BigDecimal("126.9770162"))
                .operatingHours("09:00-18:00")
                .estimatedStayTime(120)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("남산타워")
                .region("서울")
                .category("관광명소")
                .address("서울특별시 용산구 남산공원길 105")
                .latitude(new BigDecimal("37.5511694"))
                .longitude(new BigDecimal("126.9882266"))
                .operatingHours("10:00-23:00")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("북촌한옥마을")
                .region("서울")
                .category("관광명소")
                .address("서울특별시 종로구 계동길 37")
                .latitude(new BigDecimal("37.5826354"))
                .longitude(new BigDecimal("126.9859163"))
                .operatingHours("24시간")
                .estimatedStayTime(60)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("명동")
                .region("서울")
                .category("쇼핑")
                .address("서울특별시 중구 명동길")
                .latitude(new BigDecimal("37.5636250"))
                .longitude(new BigDecimal("126.9829440"))
                .operatingHours("10:00-22:00")
                .estimatedStayTime(120)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("홍대거리")
                .region("서울")
                .category("쇼핑")
                .address("서울특별시 마포구 홍익로")
                .latitude(new BigDecimal("37.5563073"))
                .longitude(new BigDecimal("126.9235349"))
                .operatingHours("24시간")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("광장시장")
                .region("서울")
                .category("맛집")
                .address("서울특별시 종로구 창경궁로 88")
                .latitude(new BigDecimal("37.5700563"))
                .longitude(new BigDecimal("126.9997916"))
                .operatingHours("09:00-23:00")
                .estimatedStayTime(60)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("이태원")
                .region("서울")
                .category("맛집")
                .address("서울특별시 용산구 이태원로")
                .latitude(new BigDecimal("37.5347129"))
                .longitude(new BigDecimal("126.9945606"))
                .operatingHours("24시간")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        log.info("서울 장소 7개 생성 완료");
    }

    private void createBusanPlaces() {
        placeRepository.save(Place.builder()
                .name("해운대해수욕장")
                .region("부산")
                .category("관광명소")
                .address("부산광역시 해운대구 해운대해변로 264")
                .latitude(new BigDecimal("35.1586977"))
                .longitude(new BigDecimal("129.1604355"))
                .operatingHours("24시간")
                .estimatedStayTime(120)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("광안리해수욕장")
                .region("부산")
                .category("관광명소")
                .address("부산광역시 수영구 광안해변로 219")
                .latitude(new BigDecimal("35.1531696"))
                .longitude(new BigDecimal("129.1186214"))
                .operatingHours("24시간")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("감천문화마을")
                .region("부산")
                .category("관광명소")
                .address("부산광역시 사하구 감내2로 203")
                .latitude(new BigDecimal("35.0973969"))
                .longitude(new BigDecimal("129.0105839"))
                .operatingHours("09:00-18:00")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("자갈치시장")
                .region("부산")
                .category("맛집")
                .address("부산광역시 중구 자갈치해안로 52")
                .latitude(new BigDecimal("35.0966244"))
                .longitude(new BigDecimal("129.0305166"))
                .operatingHours("05:00-22:00")
                .estimatedStayTime(60)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("서면")
                .region("부산")
                .category("쇼핑")
                .address("부산광역시 부산진구 서면로")
                .latitude(new BigDecimal("35.1578515"))
                .longitude(new BigDecimal("129.0598865"))
                .operatingHours("10:00-22:00")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        log.info("부산 장소 5개 생성 완료");
    }

    private void createJejuPlaces() {
        placeRepository.save(Place.builder()
                .name("성산일출봉")
                .region("제주")
                .category("관광명소")
                .address("제주특별자치도 서귀포시 성산읍 일출로 284-12")
                .latitude(new BigDecimal("33.4587216"))
                .longitude(new BigDecimal("126.9426052"))
                .operatingHours("07:00-20:00")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("한라산")
                .region("제주")
                .category("관광명소")
                .address("제주특별자치도 제주시 1100로 2070-61")
                .latitude(new BigDecimal("33.3616666"))
                .longitude(new BigDecimal("126.5291666"))
                .operatingHours("05:00-18:00")
                .estimatedStayTime(300)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("우도")
                .region("제주")
                .category("관광명소")
                .address("제주특별자치도 제주시 우도면")
                .latitude(new BigDecimal("33.5060000"))
                .longitude(new BigDecimal("126.9520000"))
                .operatingHours("24시간")
                .estimatedStayTime(240)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("협재해수욕장")
                .region("제주")
                .category("관광명소")
                .address("제주특별자치도 제주시 한림읍 협재리")
                .latitude(new BigDecimal("33.3939393"))
                .longitude(new BigDecimal("126.2394523"))
                .operatingHours("24시간")
                .estimatedStayTime(90)
                .source("visitkorea")
                .build());

        placeRepository.save(Place.builder()
                .name("동문시장")
                .region("제주")
                .category("맛집")
                .address("제주특별자치도 제주시 관덕로14길 20")
                .latitude(new BigDecimal("33.5127777"))
                .longitude(new BigDecimal("126.5272222"))
                .operatingHours("07:00-21:00")
                .estimatedStayTime(60)
                .source("visitkorea")
                .build());

        log.info("제주 장소 5개 생성 완료");
    }
}

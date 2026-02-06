package com.mysite.sbb.aitrip.trip.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(columnDefinition = "TEXT")
    private String style;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_style", nullable = false, length = 20)
    private TripStyle tripStyle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripStatus status;

    @Builder
    public Trip(User user, String title, String region, String style,
                TripStyle tripStyle, LocalDate startDate, LocalDate endDate, TripStatus status) {
        this.user = user;
        this.title = title;
        this.region = region;
        this.style = style;
        this.tripStyle = tripStyle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // 여행 정보 수정
    public void update(String title, String region, String style,
                       TripStyle tripStyle, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.region = region;
        this.style = style;
        this.tripStyle = tripStyle;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 여행 상태 변경
    public void updateStatus(TripStatus status) {
        this.status = status;
    }
}

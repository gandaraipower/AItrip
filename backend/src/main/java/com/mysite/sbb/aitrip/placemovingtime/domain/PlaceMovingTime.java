package com.mysite.sbb.aitrip.placemovingtime.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "place_moving_times",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_place_id", "to_place_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceMovingTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_place_id", nullable = false)
    private Place fromPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_place_id", nullable = false)
    private Place toPlace;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "time_minutes")
    private Integer timeMinutes;

    @Column(name = "transport_type", length = 30)
    private String transportType;

    @Builder
    public PlaceMovingTime(Place fromPlace, Place toPlace, BigDecimal distanceKm,
                           Integer timeMinutes, String transportType) {
        this.fromPlace = fromPlace;
        this.toPlace = toPlace;
        this.distanceKm = distanceKm;
        this.timeMinutes = timeMinutes;
        this.transportType = transportType;
    }
}

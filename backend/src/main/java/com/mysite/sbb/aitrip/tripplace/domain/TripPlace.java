package com.mysite.sbb.aitrip.tripplace.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trip_places",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "place_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected;

    @Builder
    public TripPlace(Trip trip, Place place, Boolean isSelected) {
        this.trip = trip;
        this.place = place;
        this.isSelected = isSelected;
    }
}

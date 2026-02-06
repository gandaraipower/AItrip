package com.mysite.sbb.aitrip.placestyletag.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "place_style_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceStyleTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false, length = 50)
    private String tag;

    private Integer frequency;

    @Column(precision = 3, scale = 2)
    private BigDecimal weight;

    @Builder
    public PlaceStyleTag(Place place, String tag, Integer frequency, BigDecimal weight) {
        this.place = place;
        this.tag = tag;
        this.frequency = frequency;
        this.weight = weight;
    }
}

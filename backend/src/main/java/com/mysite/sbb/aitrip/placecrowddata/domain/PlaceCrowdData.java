package com.mysite.sbb.aitrip.placecrowddata.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.place.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "place_crowd_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "day_of_week", "crowd_hour"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceCrowdData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "crowd_hour", nullable = false)
    private Integer hour;

    @Column(name = "crowd_level", length = 20)
    private String crowdLevel;

    @Column(name = "waiting_risk_score", precision = 3, scale = 2)
    private BigDecimal waitingRiskScore;

    @Column(name = "avg_waiting_min")
    private Integer avgWaitingMin;

    @Builder
    public PlaceCrowdData(Place place, Integer dayOfWeek, Integer hour, String crowdLevel,
                          BigDecimal waitingRiskScore, Integer avgWaitingMin) {
        this.place = place;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.crowdLevel = crowdLevel;
        this.waitingRiskScore = waitingRiskScore;
        this.avgWaitingMin = avgWaitingMin;
    }
}

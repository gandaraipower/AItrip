package com.mysite.sbb.aitrip.schedule.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import com.mysite.sbb.aitrip.place.domain.Place;
import com.mysite.sbb.aitrip.trip.domain.Trip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "schedules", indexes = {
        @Index(name = "idx_schedule_trip_day", columnList = "trip_id, day_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "visit_order", nullable = false)
    private Integer visitOrder;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "estimated_waiting_time")
    private Integer estimatedWaitingTime;

    @Column(name = "travel_time_from_prev")
    private Integer travelTimeFromPrev;

    @Column(name = "stay_duration")
    private Integer stayDuration;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder
    public Schedule(Trip trip, Place place, Integer dayNumber, Integer visitOrder,
                    LocalTime startTime, LocalTime endTime, Integer estimatedWaitingTime,
                    Integer travelTimeFromPrev, Integer stayDuration, String notes) {
        this.trip = trip;
        this.place = place;
        this.dayNumber = dayNumber;
        this.visitOrder = visitOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedWaitingTime = estimatedWaitingTime;
        this.travelTimeFromPrev = travelTimeFromPrev;
        this.stayDuration = stayDuration;
        this.notes = notes;
    }

    // 일정 수정
    public void update(Integer dayNumber, Integer visitOrder, LocalTime startTime,
                       LocalTime endTime, Integer estimatedWaitingTime,
                       Integer travelTimeFromPrev, Integer stayDuration, String notes) {
        this.dayNumber = dayNumber;
        this.visitOrder = visitOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedWaitingTime = estimatedWaitingTime;
        this.travelTimeFromPrev = travelTimeFromPrev;
        this.stayDuration = stayDuration;
        this.notes = notes;
    }
}

package com.mysite.sbb.aitrip.trip.repository;

import com.mysite.sbb.aitrip.trip.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);
}

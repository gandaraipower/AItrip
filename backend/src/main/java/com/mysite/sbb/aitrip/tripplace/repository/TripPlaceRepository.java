package com.mysite.sbb.aitrip.tripplace.repository;

import com.mysite.sbb.aitrip.tripplace.domain.TripPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripPlaceRepository extends JpaRepository<TripPlace, Long> {

    List<TripPlace> findByTripId(Long tripId);

    Optional<TripPlace> findByTripIdAndPlaceId(Long tripId, Long placeId);

    boolean existsByTripIdAndPlaceId(Long tripId, Long placeId);
}

package com.mysite.sbb.aitrip.placemovingtime.repository;

import com.mysite.sbb.aitrip.placemovingtime.domain.PlaceMovingTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceMovingTimeRepository extends JpaRepository<PlaceMovingTime, Long> {

    Optional<PlaceMovingTime> findByFromPlaceIdAndToPlaceId(Long fromPlaceId, Long toPlaceId);

    List<PlaceMovingTime> findByFromPlaceId(Long fromPlaceId);
}

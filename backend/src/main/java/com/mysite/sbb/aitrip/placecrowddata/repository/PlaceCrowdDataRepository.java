package com.mysite.sbb.aitrip.placecrowddata.repository;

import com.mysite.sbb.aitrip.placecrowddata.domain.PlaceCrowdData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceCrowdDataRepository extends JpaRepository<PlaceCrowdData, Long> {

    List<PlaceCrowdData> findByPlaceId(Long placeId);

    List<PlaceCrowdData> findByPlaceIdAndDayOfWeek(Long placeId, Integer dayOfWeek);
}

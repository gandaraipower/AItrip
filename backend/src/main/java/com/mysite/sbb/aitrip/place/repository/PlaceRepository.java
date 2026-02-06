package com.mysite.sbb.aitrip.place.repository;

import com.mysite.sbb.aitrip.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByRegion(String region);
}

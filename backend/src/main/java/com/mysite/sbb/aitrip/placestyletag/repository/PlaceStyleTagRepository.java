package com.mysite.sbb.aitrip.placestyletag.repository;

import com.mysite.sbb.aitrip.placestyletag.domain.PlaceStyleTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceStyleTagRepository extends JpaRepository<PlaceStyleTag, Long> {

    List<PlaceStyleTag> findByPlaceId(Long placeId);
}

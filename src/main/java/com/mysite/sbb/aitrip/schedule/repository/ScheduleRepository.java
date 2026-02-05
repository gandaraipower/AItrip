package com.mysite.sbb.aitrip.schedule.repository;

import com.mysite.sbb.aitrip.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTripIdOrderByDayNumberAscVisitOrderAsc(Long tripId);

    void deleteByTripId(Long tripId);
}

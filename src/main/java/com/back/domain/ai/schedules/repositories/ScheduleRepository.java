package com.back.domain.ai.schedules.repositories;

import com.back.domain.ai.schedules.entities.Schedule;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleRepository  extends CrudRepository<Schedule,Long> {
}

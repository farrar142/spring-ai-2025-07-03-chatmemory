package com.back.domain.ai.schedules.services;

import com.back.domain.ai.schedules.entities.Schedule;
import com.back.domain.ai.schedules.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    public Schedule createSchedule(String name,OffsetDateTime startAt) {
        // Convert startAtString to OffsetDateTime

        // Create a new Schedule entity
        Schedule schedule = new Schedule();
        schedule.name = name;
        schedule.startAt = startAt;

        // Save the schedule to the repository
        scheduleRepository.save(schedule);
        return schedule;
    }
    public Schedule getFirstSchedule() {
        // Retrieve the first schedule from the repository
        return scheduleRepository.findAll().iterator().next();
    }

    public void deleteSchedule(Long id) {
        // Delete the schedule by ID
        scheduleRepository.deleteById(id);
    }

    public void deleteAllSchedules() {
        // Delete all schedules
        scheduleRepository.deleteAll(
            scheduleRepository.findAll()
        );
    }

    public long countSchedules() {
        // Count all schedules
        return scheduleRepository.count();
    }
}

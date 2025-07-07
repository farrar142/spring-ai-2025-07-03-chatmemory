package com.back.domain.ai.schedules.tools;


import com.back.domain.ai.schedules.services.ScheduleService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTools {
    @Autowired
    ScheduleService scheduleService;

    @Tool
    public String createSchedule(String name,
                                 @ToolParam(description = "Time in ISO-8601 format")
                                 String startAtString) {
        // Convert startAtString to OffsetDateTime
        java.time.OffsetDateTime startAt = java.time.OffsetDateTime.parse(startAtString);

        // Create a new schedule using the service
        scheduleService.createSchedule(name, startAt);

        return "Schedule created successfully";
    }
}

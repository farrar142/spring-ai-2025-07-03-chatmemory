package com.back.domain.ai.schedules.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.GeneratedColumn;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;


@Entity
public class Schedule {
    public OffsetDateTime startAt;

    @CreatedDate
    public OffsetDateTime createdAt;
    public String name;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
}

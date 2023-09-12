package com.chobichokro.repository;

import com.chobichokro.models.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    @Override
    Optional<Schedule> findById(String string);
    List<Schedule> findByMovieName(String movieName);
    List<Schedule> findByTheaterId(String theaterId);
    List<Schedule> findByScheduleDate(String scheduleDate);
    List<Schedule> findAll();
    Boolean existsByScheduleId(String scheduleId);
    Boolean existsByMovieName(String movieName);
    Boolean existsByTheaterId(String theaterId);
    Boolean existsByScheduleDate(String scheduleDate);
    Boolean existsByHallNumber(int hallNumber);
}

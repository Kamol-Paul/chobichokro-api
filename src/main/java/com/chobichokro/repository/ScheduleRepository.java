package com.chobichokro.repository;

import com.chobichokro.models.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {


    List<Schedule> findByMovieName(String movieName);

    List<Schedule> findByTheaterId(String theaterId);

    List<Schedule> findByScheduleDate(String scheduleDate);


    Boolean existsByScheduleId(String scheduleId);

    Boolean existsByMovieName(String movieName);

    Boolean existsByTheaterId(String theaterId);

    Boolean existsByScheduleDate(String scheduleDate);

    Boolean existsByHallNumber(int hallNumber);
    List<Schedule> findAllByMovieName(String movieName);
    List<Schedule> findAllByTheaterId(String theaterId);
    List<Schedule> findAllByMovieNameAndTheaterId(String movieName, String theaterId);
    List<Schedule> findAllByMovieNameAndTheaterIdAndScheduleDateAndAndHallNumber(String movieName, String theaterId, String scheduleDate, int hallnumber);

    List<Schedule> findAllByMovieNameAndTheaterIdAndScheduleDate(String movieName, String theaterId, String showTime);
}

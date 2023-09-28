package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.models.Schedule;
import com.chobichokro.models.Theater;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

;

@RestController()
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    Helper helper;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('THEATER_OWNER')")
    public ResponseEntity<?> addSchedule(@ModelAttribute Schedule schedule, @RequestHeader("Authorization") String token) {
        return helper.myScheduleControllerHelper(schedule, token);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedules() {
        return ResponseEntity.ok(scheduleRepository.findAll());
    }

    @GetMapping("/movie/{name}")
    public ResponseEntity<?> getScheduleByMovieName(@PathVariable String name) {
        return ResponseEntity.ok(scheduleRepository.findAllByMovieName(name));
    }

    @GetMapping("/theater/{id}")
    public ResponseEntity<?> getScheduleByTheaterId(@PathVariable String id) {
        return ResponseEntity.ok(scheduleRepository.findByTheaterId(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getScheduleByDate(@PathVariable String date) {
        return ResponseEntity.ok(scheduleRepository.findByScheduleDate(date));
    }

    @GetMapping("/dropdown/{movieName}")
    public ResponseEntity<?> getScheduleByMovieNameDropdown(@PathVariable String movieName) {
        List<Schedule> schedules = scheduleRepository.findAllByMovieName(movieName);
        Set<Theater> theaterSet = new HashSet<>();
        for (Schedule schedule : schedules) {
            Optional<Theater> theater = theaterRepository.findById(schedule.getTheaterId());
            theater.ifPresent(theaterSet::add);
        }
        return ResponseEntity.ok(theaterSet);
    }

    @GetMapping("/dropdown/movieName/{movieName}/theaterId/{theaterId}")
    public ResponseEntity<?> getScheduleByMovieNameDropdown(@PathVariable String movieName, @PathVariable String theaterId) {
        List<Schedule> schedules = scheduleRepository.findAllByMovieName(movieName);
        List<List<String>> scheduleSet = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getTheaterId().equals(theaterId)) {
                List<String> alu = new ArrayList<>();
                alu.add(schedule.getScheduleId());
                alu.add(String.valueOf(schedule.getHallNumber()));
                scheduleSet.add(alu);

            }
        }
        return ResponseEntity.ok(scheduleSet);
    }

    @GetMapping("/getScheduleId")
    public ResponseEntity<?> getSchedule(@ModelAttribute Schedule schedule) {
        List<Schedule> schedules = scheduleRepository.findAllByMovieName(schedule.getMovieName());
        for (Schedule sch : schedules) {
            if (Objects.equals(sch.getTheaterId(), schedule.getTheaterId()) && Objects.equals(sch.getScheduleDate(), schedule.getScheduleDate()) && sch.getHallNumber() == schedule.getHallNumber()) {
                return ResponseEntity.ok(sch);
            }
        }
        return ResponseEntity.badRequest().body("Schedule not found");
    }


}

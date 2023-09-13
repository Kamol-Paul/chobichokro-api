package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.models.Schedule;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id) {
        Optional<Schedule> schedule = scheduleRepository.findById(id);
        if (schedule.isEmpty()) {
            return ResponseEntity.badRequest().body("Schedule not found");
        }
        return ResponseEntity.ok(scheduleRepository.findById(id));
    }

    @GetMapping("/movie/{name}")
    public ResponseEntity<?> getScheduleByMovieName(@PathVariable String name) {
        return ResponseEntity.ok(scheduleRepository.findByMovieName(name));
    }

    @GetMapping("/theater/{id}")
    public ResponseEntity<?> getScheduleByTheaterId(@PathVariable String id) {
        return ResponseEntity.ok(scheduleRepository.findByTheaterId(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getScheduleByDate(@PathVariable String date) {
        return ResponseEntity.ok(scheduleRepository.findByScheduleDate(date));
    }


}

package com.chobichokro.controllers;

import com.chobichokro.models.Schedule;
import com.chobichokro.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@ModelAttribute Schedule schedule){
        return ResponseEntity.ok(schedule);
    }
}

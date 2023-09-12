package com.chobichokro.controllers;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Schedule;
import com.chobichokro.models.Ticket;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/api/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@ModelAttribute Schedule schedule){
        boolean freeNaki = isFree(schedule);
        if(!freeNaki){
            return ResponseEntity.badRequest().body("Schedule already At that time and hall");
        }
        Movie movie = movieRepository.findByMovieName(schedule.getMovieName()).orElse(null);

        List<Ticket> for_ans = Ticket.getTicketForSchedule(schedule.getScheduleId(), 100);
        return ResponseEntity.ok(for_ans);
    }
    boolean isFree(Schedule schedule){
        return scheduleRepository.existsByHallNumber(schedule.getHallNumber())
                && scheduleRepository.existsByScheduleDate(schedule.getScheduleDate())
                && scheduleRepository.existsByTheaterId(schedule.getTheaterId());

    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedules(){
        return ResponseEntity.ok(scheduleRepository.findAll());
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id){
        Optional<Schedule> schedule = scheduleRepository.findById(id);
        if(schedule.isEmpty()){
            return ResponseEntity.badRequest().body("Schedule not found");
        }
        return ResponseEntity.ok(scheduleRepository.findById(id));
    }
    @GetMapping("/movie/{name}")
    public ResponseEntity<?> getScheduleByMovieName(@PathVariable String name){
        return ResponseEntity.ok(scheduleRepository.findByMovieName(name));
    }
    @GetMapping("/theater/{id}")
    public ResponseEntity<?> getScheduleByTheaterId(@PathVariable String id){
        return ResponseEntity.ok(scheduleRepository.findByTheaterId(id));
    }
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getScheduleByDate(@PathVariable String date){
        return ResponseEntity.ok(scheduleRepository.findByScheduleDate(date));
    }



}

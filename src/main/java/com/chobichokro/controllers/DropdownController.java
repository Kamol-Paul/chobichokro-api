package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.controllerHelper.TheaterHelper;
import com.chobichokro.models.Theater;
import com.chobichokro.payload.request.ScheduleRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/dropdown")

public class DropdownController {
    @Autowired
    Helper helper;
    @Autowired
    TheaterHelper theaterHelper;
    @GetMapping("/movie/theater")
//    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> getMovieTheater(@ModelAttribute ScheduleRequest scheduleRequest ) {
        String movieName = scheduleRequest.getMovieName();
        String theaterId = scheduleRequest.getTheaterId();
        System.out.println(movieName);
        System.out.println(theaterId);
        return theaterHelper.getMovieTheaterShowTime(movieName,theaterId);
    }

    @GetMapping("/get/schedule")
    public ResponseEntity<?> getScheduleId(@ModelAttribute ScheduleRequest scheduleRequest){
        String movieName = scheduleRequest.getMovieName();
        String date = scheduleRequest.getDate();
        int hallNumber = scheduleRequest.getHallNumber();
        String theaterId = theaterHelper.getTheaterFromTheaterOwner(scheduleRequest.getTheaterId()).getId();
        return theaterHelper.getScheduleId(movieName,theaterId,date, hallNumber);
    }

}

package com.chobichokro.controllers;


import com.chobichokro.models.Theater;
import com.chobichokro.payload.request.TheaterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chobichokro.repository.TheaterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("api/theater")
public class TheaterController {
    @Autowired
    private TheaterRepository theaterRepository;

    @GetMapping("/all")
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public Theater addTheater(@ModelAttribute TheaterRequest theaterRequest) {
        Theater theater = new Theater();
        theater.setName(theaterRequest.getName());
        theater.setAddress(theaterRequest.getAddress());
        theater.setContactNumber(theaterRequest.getContactNumber());
        return theaterRepository.save(theater);

    }
    @GetMapping("/{id}")
    public Optional<Theater> getTheaterById(@PathVariable String id){
        return theaterRepository.findById(id);
    }
    @GetMapping("/name/{name}")
    public Optional<Theater> getTheaterByName(@PathVariable String name){
        return theaterRepository.findByName(name);
    }

    @GetMapping("/query/{queryString}")
    public List<Theater> searchTheater(@PathVariable String queryString){
        List<Theater> for_ans = new ArrayList<>();
        List<Theater> allTheater = theaterRepository.findAll();
        for(Theater theater : allTheater){
            if(isSameTheater(theater, queryString)) for_ans.add(theater);
        }
        return for_ans;

    }

    private boolean isSameTheater(Theater theater, String matching){
        if(theater.getName().contains(matching)) return true;
        return theater.getAddress().contains(matching);
    }



}

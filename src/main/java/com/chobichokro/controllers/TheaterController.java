package com.chobichokro.controllers;


import com.chobichokro.models.License;
import com.chobichokro.models.Theater;
import com.chobichokro.payload.request.TheaterRequest;
import com.chobichokro.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chobichokro.repository.TheaterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/theater")
public class TheaterController {
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private LicenseRepository licenseRepository;

    @GetMapping("/all")
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTheater(@ModelAttribute TheaterRequest theaterRequest) {
        Theater theater = new Theater();
        String licenseId = theaterRequest.getLicenseId();
        if(licenseId == null || !licenseRepository.existsById(licenseId)){
            return ResponseEntity.badRequest().body("License not found");
        }
        License license = licenseRepository.findById(licenseId).orElse(null);
        if(license == null){
            return ResponseEntity.badRequest().body("License not found");
        }
        if(theaterRepository.existsByName(theaterRequest.getName())){
            return ResponseEntity.badRequest().body("Theater already exists");
        }
        System.out.println(license);
        if(!Objects.equals(license.getStatus(), "approved")){
            return ResponseEntity.badRequest().body("License not approved");
        }
        theater.setName(theaterRequest.getName());
        theater.setAddress(theaterRequest.getAddress());
        theater.setNumberOfScreens(theaterRequest.getNumberOfScreens());
        theater.setLicenseId(licenseId);
        theater = theaterRepository.save(theater);
        return ResponseEntity.ok(theater);



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

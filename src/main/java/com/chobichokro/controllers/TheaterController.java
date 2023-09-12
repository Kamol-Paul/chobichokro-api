package com.chobichokro.controllers;


import com.chobichokro.models.License;
import com.chobichokro.models.Theater;
import com.chobichokro.models.User;
import com.chobichokro.payload.request.TheaterRequest;
import com.chobichokro.repository.LicenseRepository;
import com.chobichokro.repository.UserRepository;
import com.chobichokro.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.chobichokro.repository.TheaterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/theater")
public class TheaterController {
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    com.chobichokro.security.jwt.JwtUtils jwtUtils;

    @GetMapping("/all")
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> addTheater(@ModelAttribute TheaterRequest theaterRequest, @RequestHeader("Authorization") String token) {
        Theater theater = new Theater();
        User user = userRepository.findByUsername(jwtUtils.getUserNameFromJwtToken(token.substring(7))).orElse(null);

        assert user != null;
        String licenseId = user.getLicenseId();
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
    @GetMapping("/myTheater")
    @PreAuthorize("hasRole('THEATER_OWNER')")
    ResponseEntity<?> getTheaterByOwner(@RequestHeader("Authorization") String token){
        String licenseId = authenticate(token);
        Theater theater =  theaterRepository.findByLicenseId(licenseId).orElse(null);
        return ResponseEntity.ok(Objects.requireNonNullElse(theater, "No Theater found"));
    }
    String authenticate(String authHeader){
        String token = authHeader.split(" ")[1];
        String userName =  jwtUtils.getUserNameFromJwtToken(token);
        return Objects.requireNonNull(userRepository.findByUsername(userName).orElse(null)).getLicenseId();

    }


}

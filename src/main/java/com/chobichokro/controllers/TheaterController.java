package com.chobichokro.controllers;


import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.controllerHelper.TheaterHelper;
import com.chobichokro.models.License;
import com.chobichokro.models.Theater;
import com.chobichokro.models.User;
import com.chobichokro.payload.request.TheaterRequest;
import com.chobichokro.repository.LicenseRepository;
import com.chobichokro.repository.TheaterRepository;
import com.chobichokro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/theater")
public class TheaterController {
    @Autowired
    Helper helper;
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
        if (licenseId == null || !licenseRepository.existsById(licenseId)) {
            return ResponseEntity.badRequest().body("License not found");
        }
        License license = licenseRepository.findById(licenseId).orElse(null);
        if (license == null) {
            return ResponseEntity.badRequest().body("License not found");
        }
        if (theaterRepository.existsByName(theaterRequest.getName())) {
            return ResponseEntity.badRequest().body("Theater already exists");
        }
        System.out.println(license);
        if (!Objects.equals(license.getStatus(), "approved")) {
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
    public Optional<Theater> getTheaterById(@PathVariable String id) {
        return theaterRepository.findById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Theater> getTheaterByName(@PathVariable String name) {
        return theaterRepository.findByName(name);
    }

    @GetMapping("/query/{queryString}")
    public List<Theater> searchTheater(@PathVariable String queryString) {
        List<Theater> for_ans = new ArrayList<>();
        List<Theater> allTheater = theaterRepository.findAll();
        for (Theater theater : allTheater) {
            if (isSameTheater(theater, queryString)) for_ans.add(theater);
        }
        return for_ans;

    }

    private boolean isSameTheater(Theater theater, String matching) {
        if (theater.getName().contains(matching)) return true;
        return theater.getAddress().contains(matching);
    }

    @GetMapping("/my_theater")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    ResponseEntity<?> getTheaterByOwner(@RequestHeader("Authorization") String token) {
        return theaterHelper.getMyTheater(token);

    }

    String authenticate(String authHeader) {
        String token = authHeader.split(" ")[1];
        String userName = jwtUtils.getUserNameFromJwtToken(token);
        return Objects.requireNonNull(userRepository.findByUsername(userName).orElse(null)).getLicenseId();

    }

    @PostMapping("/want_to_buy/{name}")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> whatToByeMove(@PathVariable String name, @RequestHeader("Authorization") String token) {
        String message = helper.sendBuyRequestOfMovie(name, token);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> getPendingMovies(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(helper.getPendingTheater(token));
    }

    @GetMapping("/get/new_movie")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> getNewMovies(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(helper.getNewMovies(token));
    }

    @GetMapping("/get/all_my_movie")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllTheaterOwnerMovie(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(helper.getAllTheaterOwnerMovie(token));
    }
    @Autowired
    TheaterHelper theaterHelper;
    @GetMapping("/running/{theaterId}")
    public ResponseEntity<?> getRunningMovieInTheater(@PathVariable("theaterId") String theaterId){
        return theaterHelper.getRunningMovieInTheater(theaterId);
    }
    @GetMapping("/upcoming/{theaterId}")
    public ResponseEntity<?> getUpcomingMovieInTheater(@PathVariable("theaterId") String theaterId) throws ParseException {
        return theaterHelper.getUpcomingMovieInTheater(theaterId);
    }
    @GetMapping("/get/myTheater")
    public ResponseEntity<?> getMyTheater(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(theaterHelper.getMyTheater(token));
    }
    @GetMapping("/get/running_movie")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER') or hasRole('ADMIN')")
    ResponseEntity<?> getRunningMovie(@RequestHeader("Authorization") String token) throws ParseException {
        return theaterHelper.getRunningMovie(token);
    }
    @GetMapping("/get/upcoming_movie")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER') or hasRole('ADMIN')")
    ResponseEntity<?> getUpcomingMovie(@RequestHeader("Authorization") String token){
//        return ResponseEntity.ok(helper.getNewMovies(token));
        return theaterHelper.getUpComingMovie(token);
    }
    @GetMapping("/get_analysis")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    ResponseEntity<?> getAllMovieAnalysis(@RequestHeader("Authorization") String token){
        String theaterOwner = helper.getUserId(token);
        return  ResponseEntity.ok(theaterHelper.getAllMovieAnalysis(theaterOwner));
    }
    @GetMapping("/get_analysis/{movieName}")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    ResponseEntity<?> getMovieAnalysis(@RequestHeader("Authorization") String token, @PathVariable("movieName") String movieName){
        String theaterOwner = helper.getUserId(token);
        if(theaterOwner == null){
            return  ResponseEntity.ok("Theater owner not found");
        }
        return ResponseEntity.ok(theaterHelper.getMovieAnalysis(theaterOwner, movieName));
    }
    @GetMapping("/get/analysis/{movieName}")
    @PreAuthorize("hasRole('ROLE_THEATER_OWNER')")
    ResponseEntity<?> getMovieAnalysisForTheater(@RequestHeader("Authorization") String token, @PathVariable("movieName") String movieName){
        String theaterOwner = helper.getUserId(token);
        if(theaterOwner == null) {
            return ResponseEntity.ok("Theater owner not found");
        }
        return ResponseEntity.ok(theaterHelper.getMovieAnalysisForTheater(theaterOwner, movieName));
    }


}

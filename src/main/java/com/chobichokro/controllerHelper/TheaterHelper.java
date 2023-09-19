package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.relation.TheaterOwnerMovieRelation;
import com.chobichokro.relationRepository.TheaterMoviePendingRepository;
import com.chobichokro.relationRepository.TheaterMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterNewMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterOwnerMovieRelationRepository;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TheaterHelper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TheaterMovieRelationRepository theaterMovieRelationRepository;

    @Autowired
    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TheaterMoviePendingRepository theaterMoviePendingRepository;
    @Autowired
    private TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;

    public ResponseEntity<?> getRunningMovieInTheater(String theaterId) {
        List<Schedule> schedules = scheduleRepository.findByTheaterId(theaterId);
        if(schedules == null){
            return ResponseEntity.ok("No movie running in this theater");
        }
        Set<Movie> movieSet = new HashSet<>();
        for(Schedule schedule : schedules){
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            movie.ifPresent(movieSet::add);
        }
        return  ResponseEntity.ok(movieSet);
    }

    public Set<Movie> getRunningMovieSetInTheater(String theaterId) {
        List<Schedule> schedules = scheduleRepository.findByTheaterId(theaterId);
        if(schedules == null){
            return null;
        }
        Set<Movie> movieSet = new HashSet<>();
        for(Schedule schedule : schedules){
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            movie.ifPresent(movieSet::add);
        }
        return  movieSet;
    }
    public ResponseEntity<?> getUpcomingMovie(String theaterId){
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        if(theater == null){
            return ResponseEntity.ok("Theater not found");
        }
        Optional<License> license = licenseRepository.findLicenseById(theater.getLicenseId());
        if(license.isEmpty()){
            return ResponseEntity.ok("License not found");
        }
        Optional<User> theaterOwner = userRepository.findById(license.get().getLicenseOwner());
        if(theaterOwner.isEmpty()){
            return ResponseEntity.ok("Theater owner not found");
        }
        List<TheaterOwnerMovieRelation> theaterOwnerMovieRelations = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(theaterOwner.get().getId());
        Set<Movie> movieSet = getRunningMovieSetInTheater(theaterId);
        Set<Movie> forRet = new HashSet<>();

        for(TheaterOwnerMovieRelation theaterOwnerMovieRelation: theaterOwnerMovieRelations){
            Movie movie = movieRepository.findById(theaterOwnerMovieRelation.getMovieId()).orElse(null);
            if(movie == null) continue;
            if(movieSet.contains(movie)) continue;
            forRet.add(movie);

        }
        return ResponseEntity.ok(forRet);

    }
}

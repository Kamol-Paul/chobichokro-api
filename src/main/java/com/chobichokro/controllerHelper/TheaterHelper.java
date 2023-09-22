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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TheaterHelper {
    @Autowired
    Helper helper;
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
        if (schedules == null) {
            return ResponseEntity.ok("No movie running in this theater");
        }
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        for (Schedule schedule : schedules) {
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            if (movie.isEmpty()) continue;
            if (date.before(movie.get().getReleaseDate())) continue;
            movieSet.add(movie.get());
        }
        return ResponseEntity.ok(movieSet);
    }

    public Set<Movie> getRunningMovieSetInTheater(String theaterId) throws ParseException {
        List<Schedule> schedules = scheduleRepository.findByTheaterId(theaterId);
        if (schedules == null) {
            return null;
        }
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        for (Schedule schedule : schedules) {
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            if (movie.isEmpty()) continue;
            String str = schedule.getScheduleDate();
            // 22/09/2023 8:30 am
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date date1 = formatter.parse(str);
            if (date1.before(date)) continue;
            movieSet.add(movie.get());
        }
        return movieSet;
    }

    public ResponseEntity<?> getUpcomingMovieInTheater(String theaterId) throws ParseException {
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        if (theater == null) {
            return ResponseEntity.ok("Theater not found");
        }
        Optional<License> license = licenseRepository.findLicenseById(theater.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        Optional<User> theaterOwner = userRepository.findById(license.get().getLicenseOwner());
        if (theaterOwner.isEmpty()) {
            return ResponseEntity.ok("Theater owner not found");
        }
        List<TheaterOwnerMovieRelation> theaterOwnerMovieRelations = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(theaterOwner.get().getId());
        Set<Movie> movieSet = getRunningMovieSetInTheater(theaterId);
        Set<Movie> forRet = new HashSet<>();
        Date date = new Date();
        for (TheaterOwnerMovieRelation theaterOwnerMovieRelation : theaterOwnerMovieRelations) {
            Movie movie = movieRepository.findById(theaterOwnerMovieRelation.getMovieId()).orElse(null);
            if (movie == null) continue;
            if (date.after(movie.getReleaseDate())) continue;
            forRet.add(movie);

        }
        return ResponseEntity.ok(forRet);

    }

    public ResponseEntity<?> getMyTheater(String token) {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("User not found");
        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        return ResponseEntity.ok(theaters);
    }

    public ResponseEntity<?> getRunningMovie(String token) throws ParseException {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("theater owner not found");

        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        Set<Movie> movieSet = new HashSet<>();
        for (Theater theater : theaters) {
            Set<Movie> movieSet1 = getRunningMovieSetInTheater(theater.getId());
            if (movieSet1 == null) continue;
            movieSet.addAll(movieSet1);
        }
        Set<String> movieIdSet = new HashSet<>();
        for (Movie movie : movieSet) {
            movieIdSet.add(movie.getId());
        }
        movieSet.clear();
        for (String movieId : movieIdSet) {
            Optional<Movie> movie = movieRepository.findById(movieId);
            movie.ifPresent(movieSet::add);
        }
        return ResponseEntity.ok(movieSet);

    }

    public ResponseEntity<?> getUpComingMovie(String token) {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("theater owner not found");

        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        List<TheaterOwnerMovieRelation> theaterOwnerMovieRelations = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(theaterOwner.getId());
        for(TheaterOwnerMovieRelation theaterOwnerMovieRelation: theaterOwnerMovieRelations){
            Movie movie = movieRepository.findById(theaterOwnerMovieRelation.getMovieId()).orElse(null);
            if(movie == null) continue;
            if(date.after(movie.getReleaseDate())) continue;
            movieSet.add(movie);
        }

        Set<String> movieIdSet = new HashSet<>();
        for (Movie movie : movieSet) {
            movieIdSet.add(movie.getId());
        }
        movieSet.clear();
        for (String movieId : movieIdSet) {
            Optional<Movie> movie = movieRepository.findById(movieId);
            movie.ifPresent(movieSet::add);
        }
        return ResponseEntity.ok(movieSet);

    }
}

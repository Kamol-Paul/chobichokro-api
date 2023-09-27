package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.DirectorHelper;
import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.payload.request.MovieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("api/distributor")
public class DirectorController {
    @Autowired
    DirectorHelper directorHelper;
    @Autowired
    Helper helper;

    //    @GetMapping("/get/analysis")
//    public ResponseEntity<?> getAnalysis(@RequestHeader("Authorization") String token){
//        return directorHelper.getDirectorAnalysis(token);
//    }
    @GetMapping("/get/analysis/{movieId}")
    public ResponseEntity<?> getSingleMovieAnalysis(@RequestHeader("Authorization") String token, @PathVariable String movieId) {
        return directorHelper.getSingleMovieAnalysis(token, movieId);
    }

    @GetMapping("/get/myMovies")
    public ResponseEntity<?> getMyMovies(@RequestHeader("Authorization") String token) {
        return directorHelper.getMyMovies(token);
    }

    @GetMapping("/get/me")
    public ResponseEntity<?> getMyNameId(@RequestHeader("Authorization") String token) {
        return directorHelper.getMyNameId(token);
    }

    @PostMapping("/addMovie")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_DISTRIBUTOR')")
    public ResponseEntity<?> addMovie(@ModelAttribute("movie") MovieRequest movie, @RequestHeader("Authorization") String auth) throws ParseException {
        return directorHelper.addMovie(movie, auth);
    }

    @GetMapping("/pending_movie_request")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
    ResponseEntity<?> getPendingMovie(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(helper.getPendingMovies(token));
    }

    @PostMapping("/accept_pending_request/{id}")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
    ResponseEntity<?> acceptPendingRequest(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
        return ResponseEntity.ok(helper.acceptPendingRequest(token, id));
    }

    @GetMapping("/get/running_movie")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR')")
    ResponseEntity<?> getRunningMovie(@RequestHeader("Authorization") String token) {
        return directorHelper.getRunningMovie(token);
    }

    @GetMapping("/get/upcoming_movie")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR')")
    ResponseEntity<?> getUpcomingMovie(@RequestHeader("Authorization") String token) {
        return directorHelper.getUpComingMovie(token);
    }

    @GetMapping("/get/released_movie")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR')")
    ResponseEntity<?> getRealizedMovie(@RequestHeader("Authorization") String token) {
        return directorHelper.getRealizedMovie(token);
    }

    @GetMapping("pending_movie_request/{movieName}")
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
    ResponseEntity<?> getPendingMovieByName(@RequestHeader("Authorization") String token, @PathVariable("movieName") String movieName) {
        return ResponseEntity.ok(helper.getPendingMoviesByName(token, movieName));
    }
//    @PostMapping("/send_movie/{movieId}")
//    public ResponseEntity<?> sendMovie(@PathVariable("movieId") String movieId){
//        return ResponseEntity.ok(helper.sendAllTheaterOwner(movieId));
//    }


}

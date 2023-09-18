package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.DirectorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/director")
public class DirectorController {
    @Autowired
    DirectorHelper directorHelper;
    @GetMapping("/get/analysis")
    public ResponseEntity<?> getAnalysis(@RequestHeader("Authorization") String token){
        return directorHelper.getDirectorAnalysis(token);
    }
    @GetMapping("/get/analysis/{movieId}")
    public ResponseEntity<?> getSingleMovieAanlysis(@RequestHeader("Authorization") String token, @PathVariable String movieId){
        return directorHelper.getSingleMovieAnalysis(token, movieId);
    }
    @GetMapping("/get/myMovies")
    public ResponseEntity<?> getMyMovies(@RequestHeader("Authorization") String token){
        return directorHelper.getMyMovies(token);
    }
}

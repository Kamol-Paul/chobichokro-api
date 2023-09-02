package com.chobichokro.controllers;

import com.chobichokro.models.Movie;
import com.chobichokro.payload.request.MovieRequest;
import com.chobichokro.payload.response.MovieResponse;
import com.chobichokro.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/movies")
public class MovieController {
    @Autowired
    private MovieRepository movieRepository;
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addMovie(@RequestBody MovieRequest movie) throws ParseException {
        MovieResponse movieResponse = new MovieResponse();
        System.out.println(movie);
        if (movieRepository.existsByMovieName(movie.getMovieName())) {
            movieResponse.setMessage("Movie already exists");
            return ResponseEntity.badRequest().body(movieResponse);
        }
        Movie newMovie = new Movie();
        newMovie.setMovieName(movie.getMovieName());
        newMovie.setGenre(movie.getGenre());
        newMovie.setCast(movie.getCast());
        newMovie.setDirector(movie.getDirector());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        newMovie.setReleaseDate(df.parse(movie.getReleaseDate()));
        newMovie.setTrailerLink(movie.getTrailerLink());
//        newMovie.setPosterImageLink(movie.getImage().getOriginalFilename());
        System.out.println(newMovie);
        movieRepository.save(newMovie);
        movieResponse.setMessage("Movie added successfully");
        return ResponseEntity.ok(movieResponse);
    }
}

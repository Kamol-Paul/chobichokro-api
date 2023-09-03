package com.chobichokro.controllers;

import com.chobichokro.models.Movie;
import com.chobichokro.payload.request.MovieRequest;
import com.chobichokro.payload.response.MovieResponse;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private FileServices fileServices;

    @Value("${project.image}")
    String path;
    @Autowired
    private MovieRepository movieRepository;
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addMovie(@ModelAttribute("movie") MovieRequest movie) throws ParseException {
        MovieResponse movieResponse = new MovieResponse();
//        System.out.println(movie);
        if (movieRepository.existsByMovieName(movie.getMovieName())) {
            movieResponse.setMessage("Movie already exists" + movie.getMovieName());
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
        String fileName;
        MultipartFile image = movie.getImage();
        try {
            fileName = fileServices.uploadImage(path, image);
        } catch (IOException e) {
            System.out.println("Error uploading image");
            throw new RuntimeException(e);
        }
        newMovie.setPosterImageLink(fileName);
        System.out.println(newMovie);
        movieRepository.save(newMovie);
        movieResponse.setMessage("Movie added successfully");
        return ResponseEntity.ok(movieResponse);
    }
}

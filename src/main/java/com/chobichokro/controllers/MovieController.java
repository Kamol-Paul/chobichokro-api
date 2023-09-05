package com.chobichokro.controllers;

import com.chobichokro.models.Movie;
import com.chobichokro.payload.request.MovieRequest;
import com.chobichokro.payload.response.MovieResponse;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.services.FileServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> addMovie(@ModelAttribute("movie") MovieRequest movie) throws ParseException, IOException {
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
//        movieResponse
        InputStream imageStream = fileServices.getImage(fileName);
        movieResponse.setMovieName(movie.getMovieName());
        movieResponse.setGenre(movie.getGenre());
        movieResponse.setCast(movie.getCast());
        movieResponse.setDirector(movie.getDirector());
        movieResponse.setReleaseDate(df.parse(movie.getReleaseDate()));
        movieResponse.setTrailerLink(movie.getTrailerLink());
        movieResponse.setPosterImageLink(fileName);
//        movieResponse.setImage(imageStream);
        System.out.println(movieResponse);
        return ResponseEntity.ok(movieResponse);
    }

    @GetMapping("/get/{imagePath}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    ResponseEntity<?> getImage(@PathVariable("imagePath") String imagePath, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("image/jpeg");
        httpServletResponse.getOutputStream().write(fileServices.getImage(path + File.separator + imagePath).readAllBytes());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/get/movie/{name}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    ResponseEntity<MovieResponse> getMovie(@PathVariable("name") String name){
        Optional<Movie> movie = movieRepository.findByMovieName(name);
        if(movie.isEmpty()){

            MovieResponse movieResponse = new MovieResponse("movie not found");
            return ResponseEntity.ok(movieResponse);
        }
        System.out.println(movie.get());
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setCast(movie.get().getCast());
        movieResponse.setId(movie.get().getId());
        return ResponseEntity.ok(movieResponse);

    }
    @GetMapping("/query/{queryString}")
    public List<Movie> searchMovie(@PathVariable String queryString){
        List<Movie> allMovie = movieRepository.findAll();
        List<Movie> for_ans = new ArrayList<>();
        for(Movie movie : allMovie){
            if(isSameMovie(movie, queryString)){
                for_ans.add(movie);
            }
        }
        return for_ans;

    }
    private  boolean isSameMovie(Movie movie, String matching){
        if(movie.getMovieName()!= null && movie.getMovieName().contains(matching)) return true;
        for(String genre : movie.getGenre()){
            if(genre.contains(matching)) return true;
        }
        for(String cast : movie.getCast()){
            if(cast.contains(matching)) return true;
        }
        for(String director : movie.getDirector()){
            if(director.contains(matching)) return true;
        }
        return false;
    }
}

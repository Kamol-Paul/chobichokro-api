package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.models.Movie;
import com.chobichokro.models.Review;
import com.chobichokro.models.Theater;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.ReviewRepository;
import com.chobichokro.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    TheaterRepository theaterRepository;
    @Autowired
    Helper helper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewRepository.findAll());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getReviewsByMovieId(@PathVariable("movieId") String movieId) {
        return ResponseEntity.ok(reviewRepository.findAllByMovieId(movieId));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getReviewsByUserId(@RequestHeader("Authorization") String token) {
        String userId = helper.getUserId(token);
        if (userId == null) {
            return ResponseEntity.ok("User not found");
        }
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        reviews.forEach(review -> {
            Optional<Movie> movie = movieRepository.findById(review.getMovieId());
            if (movie.isPresent()) {
                review.setMovieId(movie.get().getMovieName());
            } else {
                review.setMovieId("Movie not found");
            }
            Optional<Theater> theater = theaterRepository.findById(review.getTheatreId());
            if (theater.isPresent()) {
                review.setTheatreId(theater.get().getName());
            } else {
                review.setTheatreId("Theatre not found");
            }
        });
        System.out.println(reviews);
        return ResponseEntity.ok(reviews);
    }
//    @GetMapping("/theatre/{theatreId}")
//    public ResponseEntity<?> getReviewsByTheatreId(@PathVariable("theatreId") String theatreId) {
//        return ResponseEntity.ok(reviewRepository.findAllByTheatreId(theatreId));
//    }
}

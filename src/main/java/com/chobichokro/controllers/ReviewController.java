package com.chobichokro.controllers;

import com.chobichokro.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    ReviewRepository reviewRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewRepository.findAll());
    }
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getReviewsByMovieId(@PathVariable("movieId") String movieId) {
        return ResponseEntity.ok(reviewRepository.findAllByMovieId(movieId));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(reviewRepository.findAllByUserId(userId));
    }
    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<?> getReviewsByTheatreId(@PathVariable("theatreId") String theatreId) {
        return ResponseEntity.ok(reviewRepository.findAllByTheatreId(theatreId));
    }
}

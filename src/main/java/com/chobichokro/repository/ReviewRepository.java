package com.chobichokro.repository;

import com.chobichokro.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findAllByMovieId(String movieId);

    List<Review> findAllByUserId(String userId);
//    List<Review> findAllByTheatreId(String theatreId);
}

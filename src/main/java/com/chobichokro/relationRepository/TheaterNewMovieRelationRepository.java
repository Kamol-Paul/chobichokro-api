package com.chobichokro.relationRepository;

import com.chobichokro.relation.TheaterNewMovieRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterNewMovieRelationRepository extends MongoRepository<TheaterNewMovieRelation, String> {
    Optional<TheaterNewMovieRelation> findByTheaterOwnerIdAndNewMovieId(String theaterOwnerId, String movieId);

    List<TheaterNewMovieRelation> findAllByTheaterOwnerId(String userId);
}

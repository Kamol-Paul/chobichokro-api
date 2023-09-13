package com.chobichokro.relationRepository;

import com.chobichokro.relation.TheaterOwnerMovieRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TheaterOwnerMovieRelationRepository extends MongoRepository<TheaterOwnerMovieRelation, String> {
    List<TheaterOwnerMovieRelation> findAllByTheaterOwnerId(String theaterOwnerId);
    boolean existsByTheaterOwnerIdAndMovieId(String theaterOwnerId, String movieId);
}

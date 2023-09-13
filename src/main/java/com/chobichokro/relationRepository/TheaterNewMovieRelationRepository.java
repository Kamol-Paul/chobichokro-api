package com.chobichokro.relationRepository;

import com.chobichokro.relation.TheaterNewMovieRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TheaterNewMovieRelationRepository extends MongoRepository<TheaterNewMovieRelation, String> {
}

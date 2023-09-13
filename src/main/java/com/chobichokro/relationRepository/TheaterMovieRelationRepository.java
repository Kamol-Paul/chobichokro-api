package com.chobichokro.relationRepository;

import com.chobichokro.relation.TheaterMovieRelation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TheaterMovieRelationRepository extends MongoRepository<TheaterMovieRelation, String> {
}

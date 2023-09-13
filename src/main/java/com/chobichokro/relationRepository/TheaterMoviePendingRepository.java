package com.chobichokro.relationRepository;

import com.chobichokro.relation.TheaterMoviePending;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TheaterMoviePendingRepository extends MongoRepository<com.chobichokro.relation.TheaterMoviePending, String> {
    List<TheaterMoviePending> findAllByTheaterOwnerId(String userId);
}

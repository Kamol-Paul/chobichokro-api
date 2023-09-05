package com.chobichokro.repository;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Theater;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends MongoRepository<Theater, String> {
    public Optional<Theater> findById(String id);
    public Optional<Theater> findByName(String name);
    public boolean existsByName(String name);

    List<Theater> findAll();
}

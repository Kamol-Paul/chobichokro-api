package com.chobichokro.repository;

import com.chobichokro.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    public Optional<Movie> findByMovieName(String movieName);

    public Boolean existsByMovieName(String movieName);

    @Override
    List<Movie> findAll();


}

package com.chobichokro.repository;

import com.chobichokro.models.Theater;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends MongoRepository<Theater, String> {

    Optional<Theater> findByName(String name);

    boolean existsByName(String name);

    Optional<Theater> findByLicenseId(String licenseId);

    List<Theater> findAllByLicenseId(String licenseId);

}

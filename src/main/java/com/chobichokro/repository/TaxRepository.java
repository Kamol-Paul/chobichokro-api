package com.chobichokro.repository;

import com.chobichokro.models.Tax;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaxRepository extends MongoRepository<Tax, String> {
}

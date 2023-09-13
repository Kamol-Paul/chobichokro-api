package com.chobichokro.repository;

import com.chobichokro.models.ERole;
import com.chobichokro.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);

}

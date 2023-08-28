package com.chobichokro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chobichokro.models.ERole;
import com.chobichokro.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}

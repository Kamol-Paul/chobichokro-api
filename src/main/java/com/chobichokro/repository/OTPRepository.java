package com.chobichokro.repository;

import com.chobichokro.models.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OTPRepository extends MongoRepository<OTP, String> {
    OTP findByEmail(String email);

}

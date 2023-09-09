package com.chobichokro.repository;

import com.chobichokro.models.License;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LicenseRepository extends MongoRepository<License, String> {
    public Optional<License> findLicenseByPhoneNumber(String phoneNumber);
    public Optional<License> findLicenseByLicenseNumber(String licenseNumber);
    public Boolean existsByPhoneNumber(String phoneNumber);
    public Boolean existsByLicenseNumber(String licenseNumber);

    public Boolean existsByTransactionNumber(String transactionNumber);
    public Optional<License> findLicenseByEmail(String email);
    public Boolean existsByEmail(String email);



}

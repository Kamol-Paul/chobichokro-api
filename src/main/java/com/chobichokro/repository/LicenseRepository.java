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
    // update a license status by phone number
    public default License updateLicenseStatusByPhoneNumber(String phoneNumber, String status){
        License license = findLicenseByPhoneNumber(phoneNumber).orElse(null);
        if(license == null){
            return null;
        }
        license.setStatus(status);
        delete(license);
        return save(license);
    }

    // delete a document by phone number




}

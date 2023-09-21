package com.chobichokro.repository;

import com.chobichokro.models.License;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends MongoRepository<License, String> {
    Optional<License> findLicenseByPhoneNumber(String phoneNumber);
    Optional<License> findByLicenseNumber(String licenseNumber);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByLicenseNumber(String licenseNumber);

    boolean existsById(String id);

    Optional<License> findLicenseById(String id);

    Boolean existsByTransactionNumber(String transactionNumber);

    Optional<License> findLicenseByEmail(String email);

    Boolean existsByEmail(String email);

    // update a license status by phone number
    default License updateLicenseStatusByPhoneNumber(String phoneNumber, String status) {
        License license = findLicenseByPhoneNumber(phoneNumber).orElse(null);
        if (license == null) {
            return null;
        }
        license.setStatus(status);

        delete(license);

        return save(license);
    }

    default List<License> getPendingLicenses() {
        List<License> licenses = findAll();
        licenses.removeIf(license -> license.getStatus().equals("approved"));
        return licenses;
    }


    default List<License> getApproved(){
        List<License> licenses = findAll();
        licenses.removeIf(license -> !license.getStatus().equals("approved"));
        return licenses;

    }
}

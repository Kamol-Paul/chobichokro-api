package com.chobichokro.controllers;

import com.chobichokro.models.License;
import com.chobichokro.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/license")
public class LicenseController {
    @Autowired
    private LicenseRepository licenseRepository;

    @GetMapping("/get/{queryString}")
    public ResponseEntity<?> getLicense(@PathVariable String queryString) {
        System.out.println(queryString);
        if (queryString.contains("@")) {
            License license = licenseRepository.findLicenseByEmail(queryString).orElse(null);
            if (license == null) {
                return ResponseEntity.badRequest().body("License not found");
            }
            return ResponseEntity.ok(licenseRepository.findLicenseByEmail(queryString));
        }

        queryString = formatPhoneNumber(queryString);
        License license = licenseRepository.findLicenseByPhoneNumber(queryString).orElse(null);
        if (license == null) {
            return ResponseEntity.badRequest().body("License not found");
        }
        return ResponseEntity.ok(licenseRepository.findLicenseByPhoneNumber(queryString));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLicense(@ModelAttribute License license) {
        System.out.println(license);
        license.setPhoneNumber(formatPhoneNumber(license.getPhoneNumber()));

        if (licenseRepository.existsByLicenseNumber(license.getLicenseNumber())) {
            return ResponseEntity.badRequest().body("License already exists" + license.getLicenseNumber());
        } else if (licenseRepository.existsByPhoneNumber(license.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("License already exists" + license.getPhoneNumber());
        } else if (licenseRepository.existsByEmail(license.getEmail())) {
            return ResponseEntity.badRequest().body("License already exists" + license.getEmail());
        } else if (licenseRepository.existsByTransactionNumber(license.getTransactionNumber())) {
            return ResponseEntity.badRequest().body("License already exists" + license.getTransactionNumber());
        }

        license = licenseRepository.save(license);
        license.setLicenseOwner(license.getId());
        license = licenseRepository.save(license);
        System.out.println(license);
        return ResponseEntity.ok(license);

    }

    @GetMapping("/get/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getPendingLicense() {
        return ResponseEntity.ok(licenseRepository.getPendingLicenses());
    }

    @GetMapping("/get/approved")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getApproved() {
        return ResponseEntity.ok(licenseRepository.getApproved());
    }

    @PutMapping("/update_status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    private ResponseEntity<?> updateLicenseStatus(@RequestParam("licenseId") String licenseId, @RequestParam("status") String status) {
        System.out.println(licenseId);
        System.out.println(status);
        License license = licenseRepository.findLicenseById(licenseId).orElse(null);
        if (license == null) {
            return ResponseEntity.badRequest().body("License not found");
        }
        license.setStatus(status);
        System.out.println(license);
        license = licenseRepository.save(license);
        return ResponseEntity.ok(license);

    }

    private String formatPhoneNumber(String phoneNumber) {
        // number will be the last 10 digit
        return phoneNumber.substring(phoneNumber.length() - 10);

    }


}

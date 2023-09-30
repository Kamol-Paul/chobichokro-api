package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.TheaterHelper;
import com.chobichokro.models.License;
import com.chobichokro.models.Schedule;
import com.chobichokro.models.Theater;
import com.chobichokro.models.User;
import com.chobichokro.repository.LicenseRepository;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.TheaterRepository;
import com.chobichokro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feature")
public class FeatureController {
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    TheaterRepository theaterRepository;
    @Autowired
    TheaterHelper theaterHelper;

    @PutMapping("/make-all-id-same")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> makeAllOwnerIDToLicenseId() {
//         schedule done
        System.out.println("schedule");
        List<Schedule> scheduleList = scheduleRepository.findAll();
        for (Schedule schedule : scheduleList) {
            String theaterID = schedule.getTheaterId();
            schedule.setTheaterId(getTheaterOwner(theaterID));
            scheduleRepository.save(schedule);
        }

//         theater done
        System.out.println("theater");

        List<Theater> theatersList = theaterRepository.findAll();
        for (Theater theater : theatersList) {
            System.out.println(theater);
            String theaterOwner = getTheaterOwner(theater.getLicenseId());
            if(theaterOwner == null) continue;
            theaterRepository.delete(theater);
            theater.setLicenseId(theaterOwner);
            theater.setId(theaterOwner);
            theaterRepository.save(theater);

        }

        // license done
        System.out.println("license");

        List<License> licensesList = licenseRepository.findAll();
        licenseRepository.deleteAll(licensesList);
        for (License license : licensesList) {
            String licenseOwner = license.getLicenseOwner();
            license.setId(licenseOwner);
            licenseRepository.save(license);
        }
        System.out.println("user");

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            user.setLicenseId(user.getId());
            userRepository.save(user);
        }
        return ResponseEntity.ok(userList.toString() + licensesList.toString() + theatersList.toString());
    }

    public String getTheaterOwner(String licenseId) {
        Optional<License> license = licenseRepository.findLicenseById(licenseId);
        if(license.isPresent()){
            System.out.println(license);
        }
        else{
            System.out.println("no license");
            return null;
        }
        return licenseRepository.findById(licenseId).get().getLicenseOwner();
    }

}

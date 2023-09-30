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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<?> makeAllOwnerIDToLicenseId() {
        // schedule done
       List<Schedule> scheduleList = scheduleRepository.findAll();
       for(Schedule schedule : scheduleList){
           String theaterID = schedule.getTheaterId();
           schedule.setTheaterId(getTheaterOwner(theaterID));
           scheduleRepository.save(schedule);
       }

       // theater done
       List<Theater> theatersList = theaterRepository.findAll();
       for(Theater theater : theatersList){
           String theaterOwner = getTheaterOwner(theater.getId());
           theaterRepository.delete(theater);
           theater.setLicenseId(theaterOwner);
           theater.setId(theaterOwner);
           theaterRepository.save(theater);

       }

       // license done

       List<License> licensesList = licenseRepository.findAll();
       licenseRepository.deleteAll(licensesList);
       for(License license : licensesList){
           String licenseOwner = license.getLicenseOwner();
           license.setId(licenseOwner);
           licenseRepository.save(license);
       }

       List<User> userList = userRepository.findAll();
       for(User user : userList){
           user.setLicenseId(user.getId());
           userRepository.save(user);
       }
       return ResponseEntity.ok(userList.toString() + licensesList.toString() + theatersList.toString() + scheduleList.toString());
    }
    public String getTheaterOwner(String theaterId){
        String licenceID = theaterRepository.findById(theaterId).get().getLicenseId();
        return licenseRepository.findById(licenceID).get().getLicenseOwner();
    }

}

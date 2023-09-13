package com.chobichokro.controllerHelper;

import com.chobichokro.models.ERole;
import com.chobichokro.models.License;
import com.chobichokro.models.Role;
import com.chobichokro.models.User;
import com.chobichokro.relation.TheaterNewMovieRelation;
import com.chobichokro.relationRepository.TheaterMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterNewMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterOwnMovieRelationRepository;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Component
public class Helper {
    /*
* This class is used to store the helper methods that are used in the controllers
     */
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TheaterMovieRelationRepository theaterMovieRelationRepository;
    @Autowired
    private TheaterOwnMovieRelationRepository theaterOwnMovieRelationRepository;
    @Autowired
    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;

    public List<String> sendAllTheaterOwner(String movieId){
        List<User> allTheaterOwner = getAllTheaterOwner();
        List<String> forReturn = new ArrayList<>();
        if(allTheaterOwner == null){
            return null;
        }
        for(User user: allTheaterOwner){
            TheaterNewMovieRelation theaterNewMovieRelation = new TheaterNewMovieRelation();
            theaterNewMovieRelation.setMovieId(movieId);
            theaterNewMovieRelation.setTheaterId(user.getId());
            theaterNewMovieRelationRepository.save(theaterNewMovieRelation);
            forReturn.add(user.getUsername());
        }
        return forReturn;
    }

    public String getUserId(String authorizationToken){

        String username = jwtUtils.getUserNameFromJwtToken(authorizationToken.substring(7));
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }
    List<User> getAllTheaterOwner(){
        List<User> allUser = userRepository.findAll();
        List<User> allTheaterOwner = new ArrayList<>();
        for(User user: allUser){
            String licenseId = user.getLicenseId();
            if(licenseId != null){
                License license = licenseRepository.findById(licenseId).orElse(null);
                if(license == null) continue;
                String roleId = license.getLicenseType();
                if(Objects.equals(roleId, "theaterOwner")){
                    allTheaterOwner.add(user);
                }
            }
        }
        return allTheaterOwner;
    }


}

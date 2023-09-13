package com.chobichokro.controllerHelper;

import com.chobichokro.models.ERole;
import com.chobichokro.models.License;
import com.chobichokro.models.Role;
import com.chobichokro.models.User;
import com.chobichokro.models.Movie;
import com.chobichokro.relation.TheaterMoviePending;
import com.chobichokro.relation.TheaterNewMovieRelation;
import com.chobichokro.relation.TheaterOwnerMovieRelation;
import com.chobichokro.relationRepository.TheaterMoviePendingRepository;
import com.chobichokro.relationRepository.TheaterMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterNewMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterOwnerMovieRelationRepository;
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
    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TheaterMoviePendingRepository theaterMoviePendingRepository;
    @Autowired
    private TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;

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
    String movieIdFromMovieName(String movieName){
        Optional<Movie> movie = movieRepository.findByMovieName(movieName);
        return movie.map(Movie::getId).orElse(null);
    }
    public String sendBuyRequestOfMovie(String movieName, String theaterOwnerToken){
        String theaterOwnerId = getUserId(theaterOwnerToken);
        String movieId = movieIdFromMovieName(movieName);
        if(movieId == null){
            return "Movie not found";
        }
        if(theaterOwnerId == null){
            return "Theater owner not found";
        }
        System.out.println("Here");
        Optional<TheaterNewMovieRelation> theaterNewMovieRelation = theaterNewMovieRelationRepository.findByTheaterOwnerIdAndNewMovieId(theaterOwnerId, movieId);
        System.out.println("qyerty finished");
        if(theaterNewMovieRelation.isEmpty()) return "Movie Director does not sent you request";

        theaterNewMovieRelationRepository.delete(theaterNewMovieRelation.get());
        TheaterMoviePending theaterMoviePending = new TheaterMoviePending();
        theaterMoviePending.setMovieId(movieId);
        theaterMoviePending.setTheaterOwnerId(theaterOwnerId);
        theaterMoviePendingRepository.save(theaterMoviePending);

        return "Request sent";
    }

    public List<TheaterMoviePending> getPendingTheater(String auth){
        String userId = getUserId(auth);
        if(userId == null){
            return null;
        }
        return theaterMoviePendingRepository.findAllByTheaterOwnerId(userId);
    }


    public List<TheaterNewMovieRelation> getNewMovies(String token) {
        String userId = getUserId(token);
        if(userId == null){
            return null;
        }
        return theaterNewMovieRelationRepository.findAllByTheaterOwnerId(userId);
    }
    public List<TheaterMoviePending> getPendingMovies(String token) {
        String userId = getUserId(token);
        if(userId == null){
            return null;
        }
        List<TheaterMoviePending> all = theaterMoviePendingRepository.findAll();
        List<TheaterMoviePending> forReturn = new ArrayList<>();
        for(TheaterMoviePending theaterMoviePending : all){
            String movieId = theaterMoviePending.getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if(movie == null) continue;
            String distributorId = movie.getDistributorId();
            if(Objects.equals(distributorId, userId)){
                forReturn.add(theaterMoviePending);
            }
        }
        return forReturn;
    }
    public String acceptPendingRequest(String token, String theaterMoviePendingId){
        String userId = getUserId(token);
        if(userId == null){
            return "User not found";
        }

        Optional<TheaterMoviePending> theaterMoviePending = theaterMoviePendingRepository.findById(theaterMoviePendingId);
        if(theaterMoviePending.isEmpty()){
            return "No pending request found";
        }
        String movieId = theaterMoviePending.get().getMovieId();
        String theaterOwnerId = theaterMoviePending.get().getTheaterOwnerId();
        theaterMoviePendingRepository.delete(theaterMoviePending.get());
        TheaterOwnerMovieRelation theaterOwnerMovieRelation = new TheaterOwnerMovieRelation();
        theaterOwnerMovieRelation.setMovieId(movieId);
        theaterOwnerMovieRelation.setTheaterOwnerId(theaterOwnerId);
        theaterOwnerMovieRelationRepository.save(theaterOwnerMovieRelation);
        return "Request accepted";
    }

    public List<Movie> getAllTheaterOwnerMovie(String token){
        String userId = getUserId(token);
        if(userId == null){
            return null;
        }
        List<TheaterOwnerMovieRelation> all = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(userId);
        List<Movie> forReturn = new ArrayList<>();
        for(TheaterOwnerMovieRelation theaterOwnerMovieRelation : all){
            String movieId = theaterOwnerMovieRelation.getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if(movie == null) continue;
            forReturn.add(movie);
        }
        return forReturn;
    }
}

package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.payload.response.DistributorMovieResponse;
import com.chobichokro.payload.response.MyMovieResponse;
import com.chobichokro.payload.response.PendingResponses;
import com.chobichokro.payload.response.ScheduleResponse;
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
import org.springframework.http.ResponseEntity;
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
    public UserRepository userRepository;
    @Autowired
    public TheaterRepository theaterRepository;
    @Autowired
    public TicketRepository ticketRepository;
    @Autowired
    public MovieRepository movieRepository;
    @Autowired
    public ScheduleRepository scheduleRepository;
    @Autowired
    public TheaterMovieRelationRepository theaterMovieRelationRepository;

    @Autowired
    public TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
    @Autowired
    public LicenseRepository licenseRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public JwtUtils jwtUtils;
    @Autowired
    public TheaterMoviePendingRepository theaterMoviePendingRepository;
    @Autowired
    public TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;

    public List<String> sendAllTheaterOwner(String movieId) {
        List<User> allTheaterOwner = getAllTheaterOwner();
        System.out.println(allTheaterOwner);
        List<String> forReturn = new ArrayList<>();
        if (allTheaterOwner == null) {
            return null;
        }
        for (User user : allTheaterOwner) {
            TheaterNewMovieRelation theaterNewMovieRelation = new TheaterNewMovieRelation();
            theaterNewMovieRelation.setMovieId(movieId);
            theaterNewMovieRelation.setTheaterId(user.getId());
            theaterNewMovieRelationRepository.save(theaterNewMovieRelation);
            forReturn.add(user.getUsername());
        }
        return forReturn;
    }

    public String getUserId(String authorizationToken) {

        String username = jwtUtils.getUserNameFromJwtToken(authorizationToken.substring(7));
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }

    List<User> getAllTheaterOwner() {
        List<User> allUser = userRepository.findAll();
        List<User> allTheaterOwner = new ArrayList<>();
        Role theater_owner_role = roleRepository.findByName(ERole.ROLE_THEATER_OWNER).orElse(null);

        for (User user : allUser) {

            for (Role role : user.getRoles()) {
                assert theater_owner_role != null;
//                System.out.println(theater_owner_role);
                if (role.getName() == theater_owner_role.getName()) {
                    allTheaterOwner.add(user);
                }
            }
        }
        return allTheaterOwner;
    }

    String movieIdFromMovieName(String movieName) {
        Optional<Movie> movie = movieRepository.findByMovieName(movieName);
        return movie.map(Movie::getId).orElse(null);
    }

    public String sendBuyRequestOfMovie(String movieName, String theaterOwnerToken) {
        String theaterOwnerId = getUserId(theaterOwnerToken);
        String movieId = movieIdFromMovieName(movieName);
        if (movieId == null) {
            return "Movie not found";
        }
        if (theaterOwnerId == null) {
            return "Theater owner not found";
        }
        System.out.println("Here");
        Optional<TheaterNewMovieRelation> theaterNewMovieRelation = theaterNewMovieRelationRepository.findByTheaterOwnerIdAndNewMovieId(theaterOwnerId, movieId);
        System.out.println("qyerty finished");
        if (theaterNewMovieRelation.isEmpty()) return "Movie Director does not sent you request";

        theaterNewMovieRelationRepository.delete(theaterNewMovieRelation.get());
        TheaterMoviePending theaterMoviePending = new TheaterMoviePending();
        theaterMoviePending.setMovieId(movieId);
        theaterMoviePending.setTheaterOwnerId(theaterOwnerId);
        theaterMoviePendingRepository.save(theaterMoviePending);

        return "Request sent";
    }

    public List<TheaterMoviePending> getPendingTheater(String auth) {
        String userId = getUserId(auth);
        if (userId == null) {
            return null;
        }
        return theaterMoviePendingRepository.findAllByTheaterOwnerId(userId);
    }


    public List<Movie> getNewMovies(String token) {
        String userId = getUserId(token);
        if (userId == null) {
            return null;
        }
        List<TheaterNewMovieRelation> list = theaterNewMovieRelationRepository.findAllByTheaterOwnerId(userId);
        List<Movie> forReturn = new ArrayList<>();
        for (TheaterNewMovieRelation theaterNewMovieRelation : list) {
            String movieId = theaterNewMovieRelation.getNewMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) continue;
            movie.setStatus("new");
            forReturn.add(movie);
        }
        return forReturn;
    }

    public List<PendingResponses> getPendingMovies(String token) {
        String userId = getUserId(token);
        if (userId == null) {
            return null;
        }
        List<TheaterMoviePending> all = theaterMoviePendingRepository.findAll();
        List<PendingResponses> forReturn = new ArrayList<>();
        for (TheaterMoviePending theaterMoviePending : all) {
            String movieId = theaterMoviePending.getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) continue;
            String distributorId = movie.getDistributorId();
            if (Objects.equals(distributorId, userId)) {
                PendingResponses pendingResponses = new PendingResponses();
                pendingResponses.setId(theaterMoviePending.getId());
                pendingResponses.setMovie(movie);
                String theaterOwnerId = theaterMoviePending.getTheaterOwnerId();
                User user = userRepository.findById(theaterOwnerId).orElse(null);
                if (user == null) continue;
                Theater theater = theaterRepository.findByLicenseId(user.getLicenseId()).orElse(null);
                if (theater == null) continue;
                pendingResponses.setTheater(theater);
                forReturn.add(pendingResponses);
            }
        }
        return forReturn;
    }

    public String acceptPendingRequest(String token, String theaterMoviePendingId) {
        String userId = getUserId(token);
        if (userId == null) {
            return "User not found";
        }

        Optional<TheaterMoviePending> theaterMoviePending = theaterMoviePendingRepository.findById(theaterMoviePendingId);
        if (theaterMoviePending.isEmpty()) {
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

    public List<MyMovieResponse> getAllTheaterOwnerMovie(String token) {
        String userId = getUserId(token);
        if (userId == null) {
            return null;
        }
        List<TheaterOwnerMovieRelation> all = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(userId);
        System.out.println(all);
        List<MyMovieResponse> myMovieResponses = new ArrayList<>();
        for (TheaterOwnerMovieRelation theaterOwnerMovieRelation : all) {
            String movieId = theaterOwnerMovieRelation.getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) continue;
            String distributorId = movie.getDistributorId();
            User distributor = userRepository.findById(distributorId).orElse(null);
            assert distributor != null;
            String distributorName = distributor.getUsername();
            MyMovieResponse myMovieResponse = new MyMovieResponse();
            myMovieResponse.setMovie(movie);
            myMovieResponse.setDistributorName(distributorName);
            myMovieResponse.setTheaterOwnerId(theaterOwnerMovieRelation.getTheaterOwnerId());
            myMovieResponses.add(myMovieResponse);
        }
        return myMovieResponses;
    }

    public ResponseEntity<?> myScheduleControllerHelper(Schedule schedule, String token) {
        String userId = getUserId(token);
        if (userId == null) {
            return ResponseEntity.ok("Not valid user.");
        }
        String theaterId = schedule.getTheaterId();
        String movieName = schedule.getMovieName();
        String scheduleDate = schedule.getScheduleDate();
        int hallNumber = schedule.getHallNumber();
        if (theaterId == null || movieName == null || scheduleDate == null) {
            return ResponseEntity.ok("Invalid request");
        }
        if (!theaterRepository.existsById(theaterId)) {
            return ResponseEntity.ok("Theater not found");
        }
        Movie movie = movieRepository.findByMovieName(movieName).orElse(null);

        if (movie == null) {
            return ResponseEntity.ok("Movie not found");
        }
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        if (theater == null) {
            return ResponseEntity.ok("Theater not found");
        }
        User user = getUser(token);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }

        if (!Objects.equals(theater.getLicenseId(), user.getLicenseId())) {
            return ResponseEntity.ok("You are not the owner of this theater");
        }
        System.out.println("Theater owner id : " + userId);
        System.out.println("Movie id : " + movie.getId());
        if (!theaterOwnerMovieRelationRepository.existsByTheaterOwnerIdAndMovieId(userId, movie.getId())) {
            return ResponseEntity.ok("You Do not have the right to schedule this movie");
        }
        if (hallNumber > theater.getNumberOfScreens() || hallNumber < 1) {
            return ResponseEntity.ok("Invalid hall number");
        }

        if (isFree(schedule)) {
            return ResponseEntity.ok("Schedule already At that time and hall");
        }

        schedule = scheduleRepository.save(schedule);
        List<Ticket> for_ans = Ticket.getTicketForSchedule(schedule.getScheduleId(), 100);

        ticketRepository.saveAll(for_ans);
        ScheduleResponse scheduleResponse = new ScheduleResponse(schedule, for_ans, "Schedule added successfully");

        return ResponseEntity.ok(scheduleResponse);

    }

    boolean isFree(Schedule schedule) {
        return scheduleRepository.existsByHallNumber(schedule.getHallNumber())
                && scheduleRepository.existsByScheduleDate(schedule.getScheduleDate())
                && scheduleRepository.existsByTheaterId(schedule.getTheaterId());

    }

    User getUser(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        return userRepository.findByUsername(username).orElse(null);
    }

    public ResponseEntity<?> getRunningMovie(String theaterId) {
        var schedules = scheduleRepository.findAllByTheaterId(theaterId);
        return ResponseEntity.ok(Objects.requireNonNullElse(schedules, "No movie running"));
    }

    public ResponseEntity<?> getDistributorInformation(String movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) return ResponseEntity.ok("Movie not found");
        String distributorId = movie.getDistributorId();
        User user = userRepository.findById(distributorId).orElse(null);
        if (user == null) {
            return ResponseEntity.ok("Distributor not found");
        }
        DistributorMovieResponse distributorMovieResponse = new DistributorMovieResponse();
        distributorMovieResponse.setDistributorId(distributorId);
        distributorMovieResponse.setDistributorName(user.getUsername());
        distributorMovieResponse.setMovieName(movie.getMovieName());
        distributorMovieResponse.setCast(movie.getCast());
        distributorMovieResponse.setDirector(movie.getDirector());
        distributorMovieResponse.setGenre(movie.getGenre());
        distributorMovieResponse.setPosterImageLink(movie.getPosterImageLink());
        distributorMovieResponse.setReleaseDate(movie.getReleaseDate());
        distributorMovieResponse.setStatus(movie.getStatus());
        distributorMovieResponse.setTrailerLink(movie.getTrailerLink());
        distributorMovieResponse.setDescription(movie.getDescription());
        distributorMovieResponse.setId(movie.getId());
        return ResponseEntity.ok(distributorMovieResponse);
    }

    public ResponseEntity<?> getPendingMoviesByName(String token, String movieName) {
        String userId = getUserId(token);
        if (userId == null) {
            return ResponseEntity.ok("User not found");
        }
        List<TheaterMoviePending> all = theaterMoviePendingRepository.findAll();
        List<PendingResponses> forReturn = new ArrayList<>();
        for (TheaterMoviePending theaterMoviePending : all) {
            String movieId = theaterMoviePending.getMovieId();
            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) continue;
            if (Objects.equals(movie.getMovieName(), movieName)) {
                String distributorId = movie.getDistributorId();
                if (Objects.equals(distributorId, userId)) {
                    PendingResponses pendingResponses = new PendingResponses();
                    pendingResponses.setId(theaterMoviePending.getId());
                    pendingResponses.setMovie(movie);
                    String theaterOwnerId = theaterMoviePending.getTheaterOwnerId();
                    User user = userRepository.findById(theaterOwnerId).orElse(null);
                    if (user == null) continue;
                    Theater theater = theaterRepository.findByLicenseId(user.getLicenseId()).orElse(null);
                    if (theater == null) continue;
                    pendingResponses.setTheater(theater);
                    forReturn.add(pendingResponses);
                }
            }
        }
        return ResponseEntity.ok(forReturn);

    }
}

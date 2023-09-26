package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.payload.response.MovieAnalysis;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TheaterHelper {
    @Autowired
    Helper helper;
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
    @Autowired
    private ReviewRepository reviewRepository;

    public ResponseEntity<?> getRunningMovieInTheater(String theaterId) {
        List<Schedule> schedules = scheduleRepository.findByTheaterId(theaterId);
        if (schedules == null) {
            return ResponseEntity.ok("No movie running in this theater");
        }
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        for (Schedule schedule : schedules) {
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            if (movie.isEmpty()) continue;
            if (date.before(movie.get().getReleaseDate())) continue;
            movie.get().setStatus("running");
            movieRepository.save(movie.get());
            movieSet.add(movie.get());
        }
        return ResponseEntity.ok(movieSet);
    }

    public Set<Movie> getRunningMovieSetInTheater(String theaterId) throws ParseException {
        List<Schedule> schedules = scheduleRepository.findByTheaterId(theaterId);
        if (schedules == null) {
            return null;
        }
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        for (Schedule schedule : schedules) {
            Optional<Movie> movie = movieRepository.findByMovieName(schedule.getMovieName());
            if (movie.isEmpty()) continue;
            String str = schedule.getScheduleDate();
            // 22/09/2023 8:30 am
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date date1 = formatter.parse(str);
            if (date1.before(date)) continue;
            movie.get().setStatus("running");
            movieRepository.save(movie.get());
            movieSet.add(movie.get());
        }
        return movieSet;
    }

    public ResponseEntity<?> getUpcomingMovieInTheater(String theaterId) throws ParseException {
        Theater theater = theaterRepository.findById(theaterId).orElse(null);
        if (theater == null) {
            return ResponseEntity.ok("Theater not found");
        }
        Optional<License> license = licenseRepository.findLicenseById(theater.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        Optional<User> theaterOwner = userRepository.findById(license.get().getLicenseOwner());
        if (theaterOwner.isEmpty()) {
            return ResponseEntity.ok("Theater owner not found");
        }
        List<TheaterOwnerMovieRelation> theaterOwnerMovieRelations = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(theaterOwner.get().getId());
        Set<Movie> movieSet = getRunningMovieSetInTheater(theaterId);
        Set<Movie> forRet = new HashSet<>();
        Date date = new Date();
        for (TheaterOwnerMovieRelation theaterOwnerMovieRelation : theaterOwnerMovieRelations) {
            Movie movie = movieRepository.findById(theaterOwnerMovieRelation.getMovieId()).orElse(null);
            if (movie == null) continue;
            if (date.after(movie.getReleaseDate())) continue;
            forRet.add(movie);

        }
        return ResponseEntity.ok(forRet);

    }

    public ResponseEntity<?> getMyTheater(String token) {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("User not found");
        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        return ResponseEntity.ok(theaters);
    }

    public ResponseEntity<?> getRunningMovie(String token) throws ParseException {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("theater owner not found");

        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        Set<Movie> movieSet = new HashSet<>();
        System.out.println(theaters);
        for (Theater theater : theaters) {
            Set<Movie> movieSet1 = getRunningMovieSetInTheater(theater.getId());
            if (movieSet1 == null) continue;
            movieSet.addAll(movieSet1);
        }
//        System.out.println(movieSet);
        Set<String> movieIdSet = new HashSet<>();
        for (Movie movie : movieSet) {
            movieIdSet.add(movie.getId());
        }
        movieSet.clear();
        for (String movieId : movieIdSet) {
            Optional<Movie> movie = movieRepository.findById(movieId);
            movie.ifPresent(movieSet::add);
        }
        return ResponseEntity.ok(movieSet);

    }

    public ResponseEntity<?> getUpComingMovie(String token) {
        User theaterOwner = helper.getUser(token);
        if (theaterOwner == null) {
            return ResponseEntity.ok("theater owner not found");

        }
        Optional<License> license = licenseRepository.findLicenseById(theaterOwner.getLicenseId());
        if (license.isEmpty()) {
            return ResponseEntity.ok("License not found");
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(license.get().getId());
        Set<Movie> movieSet = new HashSet<>();
        Date date = new Date();
        List<TheaterOwnerMovieRelation> theaterOwnerMovieRelations = theaterOwnerMovieRelationRepository.findAllByTheaterOwnerId(theaterOwner.getId());
        for (TheaterOwnerMovieRelation theaterOwnerMovieRelation : theaterOwnerMovieRelations) {
            Movie movie = movieRepository.findById(theaterOwnerMovieRelation.getMovieId()).orElse(null);
            if (movie == null) continue;
            if (date.after(movie.getReleaseDate())) continue;
            movie.setStatus("upcoming");
            movieRepository.save(movie);
            movieSet.add(movie);
        }

        Set<String> movieIdSet = new HashSet<>();
        for (Movie movie : movieSet) {
            movieIdSet.add(movie.getId());
        }
        movieSet.clear();
        for (String movieId : movieIdSet) {
            Optional<Movie> movie = movieRepository.findById(movieId);
            movie.ifPresent(movieSet::add);
        }
        return ResponseEntity.ok(movieSet);

    }

    public ResponseEntity<?> getMovieTheaterShowTime(String movieName, String theaterOwnerId) {
        Theater theater = getTheaterFromTheaterOwner(theaterOwnerId);
        if (theater == null) {
            return ResponseEntity.ok("getting theater has a problem.");
        }
        System.out.println(theater);
        List<Schedule> schedules = scheduleRepository.findAllByMovieName(movieName);
        String theaterId = theater.getId();
        List<Map<String, String>> forReturn = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getTheaterId().equals(theaterId)) {
                Map<String, String> potol = new HashMap<>();
                potol.put("showtime", schedule.getScheduleDate());
                potol.put("hallNumber", String.valueOf(schedule.getHallNumber()));
                forReturn.add(potol);
                System.out.println(potol);

            }
        }
        return ResponseEntity.ok(forReturn);

    }

    public Theater getTheaterFromTheaterOwner(String theaterOwnerId) {
        Optional<User> theaterOwner = userRepository.findById(theaterOwnerId);
        if (theaterOwner.isEmpty()) {
            return null;
        }
        List<Theater> theaters = theaterRepository.findAllByLicenseId(theaterOwner.get().getLicenseId());
        if (theaters == null) {
            return null;

        }
        return theaters.get(0);
    }

    public ResponseEntity<?> getScheduleId(String movieName, String theaterId, String date, int hallNumber) {
        List<Schedule> schedules = scheduleRepository.findAllByMovieNameAndTheaterIdAndScheduleDateAndAndHallNumber(movieName, theaterId, date, hallNumber);
        if (schedules.isEmpty()) {
            return ResponseEntity.ok("Schedule not found");
        }
        return ResponseEntity.ok(schedules);
    }

    public MovieAnalysis getMovieAnalysis(String theaterId, String movieName) {
        Movie movie = movieRepository.findByMovieName(movieName).orElse(null);
        if (movie == null) return null;
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieNameAndTheaterId(movieName, theaterId);
        Set<String> theaterIdSet = new HashSet<>();
        List<Ticket> tickets = ticketRepository.findAll();
        MovieAnalysis movieAnalysis = new MovieAnalysis();
        movieAnalysis.setMovie(movie);
        movieAnalysis.setTotalScreening(scheduleList.size());
        int totalTicketSell = 0;
        int totalRevenue = 0;
        int ticketPrice = 100;
        for (Schedule schedule : scheduleList) {
            theaterIdSet.add(schedule.getTheaterId());
            int countTicket = countTickerOfSchedule(tickets, schedule.getScheduleId());
            totalTicketSell += countTicket;
            totalRevenue += totalTicketSell * ticketPrice;

        }
        movieAnalysis.setTotalTheater(theaterIdSet.size());
        movieAnalysis.setTotalRevenue(totalRevenue);
        movieAnalysis.setTotalTicket(totalTicketSell);
        List<Review> reviews = reviewRepository.findAllByMovieId(movie.getId());
        movieAnalysis.setReviews(reviews);
        if (!reviews.isEmpty()) {
            double totalRating = 0;
            for (Review review : reviews) {
                totalRating += review.getSentimentScore();
            }
            movieAnalysis.setAverageSentiment(totalRating / reviews.size());
        } else {
            movieAnalysis.setAverageSentiment(0);
        }
        return movieAnalysis;
    }

    int countTickerOfSchedule(List<Ticket> tickets, String scheduleId) {
        int count = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getScheduleId().equals(scheduleId) && ticket.isBooked()) {
                count++;
            }
        }
        return count;

    }
    public Map<String, MovieAnalysis> getAllMovieAnalysis(String theaterId){
        List<Schedule> schedules = scheduleRepository.findAllByTheaterId(theaterId);
        Map<String, MovieAnalysis> forReturn = new HashMap<>();
        for(Schedule schedule: schedules){
            String movieName = schedule.getMovieName();
            if(!forReturn.containsKey(movieName))
                forReturn.put(movieName, getMovieAnalysis(theaterId,movieName));

        }
        return forReturn;
    }

    public ResponseEntity<?> getMovieAnalysisForTheater(String id, String movieName) {
        List<Schedule> schedules = scheduleRepository.findAllByMovieNameAndTheaterId(movieName, id);
        if(schedules.isEmpty()) return null;

        int totalTicket = 0;
        for(Schedule schedule : schedules){
            List<Ticket> tickets = ticketRepository.findAllByScheduleId(schedule.getScheduleId());
            for(Ticket ticket : tickets){
                if(ticket.isBooked()) totalTicket++;
            }
        }
        return ResponseEntity.ok(totalTicket);
    }
}

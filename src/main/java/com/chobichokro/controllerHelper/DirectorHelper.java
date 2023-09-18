package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.payload.response.DirectorAnalysis;
import com.chobichokro.payload.response.MovieAnalysis;
import com.chobichokro.relationRepository.TheaterMoviePendingRepository;
import com.chobichokro.relationRepository.TheaterMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterNewMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterOwnerMovieRelationRepository;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DirectorHelper {
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
    private  ReviewRepository reviewRepository;

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

    public ResponseEntity<?> getDirectorAnalysis(String token) {
        User director = getMe(token);
        if (director == null) {
            return ResponseEntity.ok("Director not found");
        }
        DirectorAnalysis directorAnalysis = new DirectorAnalysis();
        directorAnalysis.setDirectorName(director.getUsername());
        List<MovieAnalysis> movieAnalysisList = new ArrayList<>();
//        directorAnalysis.setMovieAnalysisList();
        List<Movie> movieList = movieRepository.findAllByDistributorId(director.getId());
        System.out.println(movieList);
        directorAnalysis.setTotalMovie(movieList.size());
        List<Ticket> tickets = ticketRepository.findAll();
        for (Movie movie : movieList) {
            List<Schedule> scheduleList = scheduleRepository.findAllByMovieName(movie.getMovieName());
            Set<String> theaterIdSet = new HashSet<>();

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
            if(!reviews.isEmpty()){
                double totalRating = 0;
                for(Review review: reviews){
                    totalRating+=review.getSentimentScore();
                }
                movieAnalysis.setAverageSentiment(totalRating/reviews.size());
            }else {
                movieAnalysis.setAverageSentiment(0);
            }
            movieAnalysisList.add(movieAnalysis);

        }
        directorAnalysis.setMovieAnalysisList(movieAnalysisList);
        return ResponseEntity.ok(directorAnalysis);

    }
    public ResponseEntity<?> getSingleMovieAnalysis(String token, String movieId){
        User director = getMe(token);
        if (director == null) {
            return ResponseEntity.ok("Director not found");
        }
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if(movie == null){
            return ResponseEntity.ok("Movie not found");
        }
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieName(movie.getMovieName());
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
        if(!reviews.isEmpty()){
            double totalRating = 0;
            for(Review review: reviews){
                totalRating+=review.getSentimentScore();
            }
            movieAnalysis.setAverageSentiment(totalRating/reviews.size());
        }else {
            movieAnalysis.setAverageSentiment(0);
        }
        return ResponseEntity.ok(movieAnalysis);
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

    public User getMe(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);

    }

    public ResponseEntity<?> getMyMovies(String token) {
        User user = getMe(token);
        if(user == null){
            return ResponseEntity.ok("User not found");
        }
        List<Movie> movieList = movieRepository.findAllByDistributorId(user.getId());
        return ResponseEntity.ok(movieList);
    }
}

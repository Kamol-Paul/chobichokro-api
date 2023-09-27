package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.payload.request.MovieRequest;
import com.chobichokro.payload.response.DirectorAnalysis;
import com.chobichokro.payload.response.MovieAnalysis;
import com.chobichokro.payload.response.MovieResponse;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import com.chobichokro.services.FileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DirectorHelper {
    @Value("${project.image}")
    String path;
    //    @Autowired
//    private TheaterMoviePendingRepository theaterMoviePendingRepository;
//    @Autowired
//    private TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;
    @Autowired
    FileServices fileServices;
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
    //    @Autowired
//    private TheaterMovieRelationRepository theaterMovieRelationRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    //    @Autowired
//    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
//    @Autowired
//    private LicenseRepository licenseRepository;
//    @Autowired
//    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;

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
            movieAnalysisList.add(movieAnalysis);

        }
        directorAnalysis.setMovieAnalysisList(movieAnalysisList);
        return ResponseEntity.ok(directorAnalysis);

    }

    public ResponseEntity<?> getSingleMovieAnalysis(String token, String movieId) {
        User director = getMe(token);
        if (director == null) {
            return ResponseEntity.ok("Distributor not found");
        }
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            return ResponseEntity.ok("Movie not found");
        }
        if (Objects.equals(movie.getStatus(), "Released")) {
            return getSingleMovieAnalysis(movie);
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
        List<Theater> theaters = new ArrayList<>();
        for (String id : theaterIdSet) {
            theaterRepository.findById(id).ifPresent(theaters::add);
        }
        movieAnalysis.setTheaters(theaters);
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
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        List<Movie> movieList = movieRepository.findAllByDistributorId(user.getId());
        return ResponseEntity.ok(movieList);
    }

    public ResponseEntity<?> getMyNameId(String token) {
        User user = getMe(token);
        Map<String, String> map = new HashMap<>();
        map.put("name", user.getUsername());
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        return ResponseEntity.ok(map);
    }

    public ResponseEntity<?> addMovie(MovieRequest movie, String auth) throws ParseException {

        MovieResponse movieResponse = new MovieResponse();
        String distributorId = getMe(auth).getId();
        if (distributorId == null) {
            movieResponse.setMessage("Distributor id is null");
            return ResponseEntity.badRequest().body(movieResponse);
        }
        User distributor = userRepository.findById(distributorId).orElse(null);
        if (distributor == null) {
            movieResponse.setMessage("Distributor not found");
            return ResponseEntity.badRequest().body(movieResponse);
        }
        if (movieRepository.existsByMovieName(movie.getMovieName())) {
            movieResponse.setMessage("Movie already exists" + movie.getMovieName());
            return ResponseEntity.badRequest().body(movieResponse);
        }
        Movie newMovie = new Movie();

        newMovie.setMovieName(movie.getMovieName());
        newMovie.setGenre(movie.getGenre());
        newMovie.setCast(movie.getCast());
        newMovie.setDirector(movie.getDirector());
        newMovie.setCost(movie.getCost());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        newMovie.setReleaseDate(df.parse(movie.getReleaseDate()));
        newMovie.setTrailerLink(movie.getTrailerLink());
        String fileName;
        MultipartFile image = movie.getImage();
        try {
            fileName = fileServices.uploadImage(path, image);
        } catch (IOException e) {
            System.out.println("Error uploading image");
            throw new RuntimeException(e);
        }
        newMovie.setPosterImageLink(fileName);
        System.out.println(newMovie);
        newMovie.setStatus(movie.getStatus());
        newMovie.setDescription(movie.getDescription());
        newMovie.setDistributorId(distributorId);
        newMovie = movieRepository.save(newMovie);
        movieResponse.setMessage("Movie added successfully");
        movieResponse.setMovieName(movie.getMovieName());
        movieResponse.setGenre(movie.getGenre());
        movieResponse.setCast(movie.getCast());
        movieResponse.setDirector(movie.getDirector());
        movieResponse.setReleaseDate(df.parse(movie.getReleaseDate()));
        movieResponse.setTrailerLink(movie.getTrailerLink());
        movieResponse.setPosterImageLink(fileName);
        movieResponse.setStatus(movie.getStatus());
        movieResponse.setDescription(movie.getDescription());
        movieResponse.setDistributorId(distributorId);
        movieResponse.setCost(movie.getCost());
        System.out.println(movieResponse);
        movieResponse.setTheaterOwnerToSend(helper.sendAllTheaterOwner(newMovie.getId()));
        return ResponseEntity.ok(movieResponse);
    }

    public ResponseEntity<?> getRunningMovie(String token) {
        User user = getMe(token);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        List<Movie> movieList = movieRepository.findAllByDistributorId(user.getId());
        List<Movie> runningMovieList = new ArrayList<>();
        List<Schedule> scheduleList = scheduleRepository.findAll();
        Set<String> runningMovieName = new HashSet<>();
        scheduleList.forEach(schedule -> {
            runningMovieName.add(schedule.getMovieName());
        });
        for (Movie movie : movieList) {

            if (runningMovieName.contains(movie.getMovieName())) {
                runningMovieList.add(movie);
            }

        }
        return ResponseEntity.ok(runningMovieList);
    }

    public ResponseEntity<?> getUpComingMovie(String token) {
        User user = getMe(token);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        List<Movie> movieList = movieRepository.findAllByDistributorId(user.getId());
        List<Movie> upComingMovieList = new ArrayList<>();
        Date currentDate = new Date();
        List<Schedule> scheduleList = scheduleRepository.findAll();
        for (Movie movie : movieList) {
            Date date = movie.getReleaseDate();
            if (date.after(currentDate)) {
                upComingMovieList.add(movie);
            }
        }
        return ResponseEntity.ok(upComingMovieList);
    }

    public ResponseEntity<?> getRealizedMovie(String token) {
        User user = getMe(token);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        List<Movie> movieList = movieRepository.findAllByDistributorId(user.getId());
        List<Movie> realizedMovieList = new ArrayList<>();
        Date currentDate = new Date();
        for (Movie movie : movieList) {
            Date date = movie.getReleaseDate();
            if (date.before(currentDate)) {
                realizedMovieList.add(movie);
            }
        }
        return ResponseEntity.ok(realizedMovieList);

    }

    public ResponseEntity<?> getSingleMovieAnalysis(Movie movie) {
        MovieAnalysis movieAnalysis = new MovieAnalysis();
        movieAnalysis.setMovie(movie);
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
        movieAnalysis.setTotalScreening(140);
        movieAnalysis.setTotalTheater(184);
        movieAnalysis.setTotalTicket(100000);
        movieAnalysis.setTotalRevenue(41e+7);
        double cost = movie.getCost();
        double ratio = movieAnalysis.getTotalRevenue() / cost;
        if (ratio < 0.5) {
            movieAnalysis.setMovieVerdict("Disaster");
        } else if (ratio < 1.1) {
            movieAnalysis.setMovieVerdict("Flop");
        } else if (ratio < 1.5) {
            movieAnalysis.setMovieVerdict("Average");
        } else if (ratio < 2.5) {
            movieAnalysis.setMovieVerdict("Hit");
        } else if (ratio < 5) {
            movieAnalysis.setMovieVerdict("Super Hit");

        } else if (ratio < 20) {
            movieAnalysis.setMovieVerdict("Block Bluster");
        } else {
            movieAnalysis.setMovieVerdict("Industry Hit");
        }
        return ResponseEntity.ok(movieAnalysis);
    }

}

package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.Helper;
import com.chobichokro.models.Movie;
import com.chobichokro.models.Schedule;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.UserRepository;
import com.chobichokro.security.jwt.JwtUtils;
import com.chobichokro.services.FileServices;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

//@CrossOrigin(origins = {"*", "http://localhost:3000"})
@RestController
@RequestMapping("api/movies")
public class MovieController {
    @Value("${project.image}")
    String path;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private Helper helper;
    @Autowired
    private FileServices fileServices;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;


//    @PostMapping("/add")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_DISTRIBUTOR')")
//    public ResponseEntity<?> addMovie(@ModelAttribute("movie") MovieRequest movie, @RequestHeader("Authorization") String auth) throws ParseException {
//
//        MovieResponse movieResponse = new MovieResponse();
//        String distributorId = helper.getUserId(auth);
//        System.out.println(distributorId);
//        if (distributorId == null) {
//            movieResponse.setMessage("Distributor id is null");
//            return ResponseEntity.badRequest().body(movieResponse);
//        }
//        User distributor = userRepository.findById(distributorId).orElse(null);
//        if (distributor == null) {
//            movieResponse.setMessage("Distributor not found");
//            return ResponseEntity.badRequest().body(movieResponse);
//        }
////        System.out.println(movie);
//        if (movieRepository.existsByMovieName(movie.getMovieName())) {
//            movieResponse.setMessage("Movie already exists" + movie.getMovieName());
//            return ResponseEntity.badRequest().body(movieResponse);
//        }
//        Movie newMovie = new Movie();
//
//        newMovie.setMovieName(movie.getMovieName());
//        newMovie.setGenre(movie.getGenre());
//        newMovie.setCast(movie.getCast());
//        newMovie.setDirector(movie.getDirector());
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        newMovie.setReleaseDate(df.parse(movie.getReleaseDate()));
//        newMovie.setTrailerLink(movie.getTrailerLink());
//        String fileName;
//        MultipartFile image = movie.getImage();
//        try {
//            fileName = fileServices.uploadImage(path, image);
//        } catch (IOException e) {
//            System.out.println("Error uploading image");
//            throw new RuntimeException(e);
//        }
//        newMovie.setPosterImageLink(fileName);
//        System.out.println(newMovie);
//        newMovie.setStatus(movie.getStatus());
//        newMovie.setDescription(movie.getDescription());
//        newMovie.setDistributorId(distributorId);
//        movieRepository.save(newMovie);
//        movieResponse.setMessage("Movie added successfully");
//        movieResponse.setMovieName(movie.getMovieName());
//        movieResponse.setGenre(movie.getGenre());
//        movieResponse.setCast(movie.getCast());
//        movieResponse.setDirector(movie.getDirector());
//        movieResponse.setReleaseDate(df.parse(movie.getReleaseDate()));
//        movieResponse.setTrailerLink(movie.getTrailerLink());
//        movieResponse.setPosterImageLink(fileName);
//        movieResponse.setStatus(movie.getStatus());
//        movieResponse.setDescription(movie.getDescription());
//        movieResponse.setDistributorId(distributorId);
//        System.out.println(movieResponse);
////        helper.sendAllTheaterOwner(newMovie.getId());
//        movieResponse.setTheaterOwnerToSend(helper.sendAllTheaterOwner(newMovie.getId()));
//        return ResponseEntity.ok(movieResponse);
//    }
    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/all")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/get/{imagePath}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    ResponseEntity<?> getImage(@PathVariable("imagePath") String imagePath, HttpServletResponse httpServletResponse) throws IOException {
        System.out.println(imagePath);
        httpServletResponse.setContentType("image/jpeg");
        httpServletResponse.getOutputStream().write(fileServices.getImage(path + File.separator + imagePath).readAllBytes());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get/movie/{name}")
    ResponseEntity<?> getMovie(@PathVariable("name") String name) {
        Optional<Movie> movie = movieRepository.findByMovieName(name);
        if (movie.isEmpty()) {

//            MovieResponse movieResponse = new MovieResponse("movie not found");
            return ResponseEntity.ok("movie not found");
        }
        return ResponseEntity.ok(movie);

    }

//    @GetMapping("/myMovie")
//    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
//    ResponseEntity<?> getMyMovie(@RequestHeader("Authorization") String token) {
//        System.out.println("token :" + token);
//        return ResponseEntity.ok(movieRepository.findAllByDistributorId(authenticate(token)));
//    }

    @GetMapping("/query/{queryString}")
    public List<Movie> searchMovie(@PathVariable String queryString) {
        List<Movie> allMovie = movieRepository.findAll();
        System.out.println(allMovie);
        List<Movie> for_ans = new ArrayList<>();
        for (Movie movie : allMovie) {
            if (isSameMovie(movie, queryString)) {
                for_ans.add(movie);
            }
        }
        System.out.println(for_ans);
        return for_ans;

    }

//    String authenticate(String authHeader) {
//        String token = authHeader.split(" ")[1];
//        String userName = jwtUtils.getUserNameFromJwtToken(token);
//        return Objects.requireNonNull(userRepository.findByUsername(userName).orElse(null)).getId();
//
//    }

//    @GetMapping("/pending_movie_request")
//    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
//    ResponseEntity<?> getPendingMovie(@RequestHeader("Authorization") String token) {
//        System.out.println("token :" + token);
//        return ResponseEntity.ok(helper.getPendingMovies(token));
//    }

//    @PostMapping("/accept_pending_request/{id}")
//    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR') or hasRole('ADMIN')")
//    ResponseEntity<?> acceptPendingRequest(@RequestHeader("Authorization") String token, @PathVariable("id") String id) {
//        return ResponseEntity.ok(helper.acceptPendingRequest(token, id));
//    }

    private boolean isSameMovie(Movie movie, String matching) {
        matching = matching.toLowerCase();
        if (movie.getMovieName() != null && movie.getMovieName().toLowerCase().contains(matching)) return true;
        if (movie.getGenre() != null) {
            for (String genre : movie.getGenre()) {
                if (genre.toLowerCase().contains(matching)) return true;
            }
        }
        if (movie.getCast() != null) {
            for (String cast : movie.getCast()) {
                if (cast.toLowerCase().contains(matching)) return true;
            }
        }

        if (movie.getDirector() != null) {
            for (String director : movie.getDirector()) {
                if (director.toLowerCase().contains(matching)) return true;
            }
        }

        return false;
    }

    @PostMapping("/get_movie_id/{movieName}")
    ResponseEntity<?> getMovieId(@PathVariable("movieName") String movieName) {
        Optional<Movie> movie = movieRepository.findByMovieName(movieName);
        return movie.<ResponseEntity<?>>map(value -> ResponseEntity.ok(value.getId())).orElseGet(() -> ResponseEntity.ok("movie not found"));
    }

    //    @GetMapping("/get/running_movie/theater/{id}")
//    ResponseEntity<?> getRunningMovie(@PathVariable("id") String id){
//        return helper.getRunningMovie(id);
//    }
    @GetMapping("/get/distributor_information/{movieId}")
    ResponseEntity<?> getDistributorInformation(@PathVariable("movieId") String movieId) {
        return helper.getDistributorInformation(movieId);
    }

    @GetMapping("/get/running")
    ResponseEntity<?> getRunningMovie() {
        List<Schedule> allSchedule = scheduleRepository.findAll();
        Set<String> movieNames = new HashSet<>();
        allSchedule.forEach(schedule -> {
            movieNames.add(schedule.getMovieName());
        });
        List<Movie> forReturn = new ArrayList<>();
        for (String movieName : movieNames) {
            movieRepository.findByMovieName(movieName).ifPresent(forReturn::add);
        }
        return ResponseEntity.ok(forReturn);
    }

//    @GetMapping("/get/released")
//    ResponseEntity<?> getReleasedMovies(){
//        List<Movie> allMovie = movieRepository.findAll();
//        List<Movie> forReturn = new ArrayList<>();
//        for (Movie movie : allMovie) {
//            if(movie.getStatus().equals("Released")) forReturn.add(movie);
//        }
//        return ResponseEntity.ok(forReturn);
//    }
//    @PutMapping("/")
//    public ResponseEntity<?> updateMovie(){
//       List<Movie> allMovie = movieRepository.findAll();
//         allMovie.forEach(movie -> {
//              movie.setCost(2.5e7);
//              movieRepository.save(movie);
//         });
//         return ResponseEntity.ok(movieRepository.findAll());
//    }
}

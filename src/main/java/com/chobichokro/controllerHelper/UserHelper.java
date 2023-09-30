package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.payload.request.ReviewRequest;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Component
public class UserHelper {
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
//
//    @Autowired
//    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
    @Autowired
    private LicenseRepository licenseRepository;
    //    @Autowired
//    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    //    @Autowired
//    private TheaterMoviePendingRepository theaterMoviePendingRepository;
//    @Autowired
//    private TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;
    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public User getMe(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);

    }

//    public Map<String,String> setAmount(String token, Double amount) {
//        Map<String , String> forReturn = new HashMap<>();
//        User user = getMe(token);
//        if (user == null) {
//            forReturn.put("message" , "user not found");
//            return forReturn;
//        }
//        user.setAmountBalance(amount);
//        user = userRepository.save(user);
//
//        forReturn.put("userName", user.getUsername());
//        forReturn.put("email", user.getEmail());
//        forReturn.put("amountBalance", String.valueOf(user.getAmountBalance()));
//        forReturn.put("message", "ammount set successfully");
//
//        return forReturn;
//    }

    public List<Schedule> getAllSchedule() {
        return scheduleRepository.findAll();
    }

    public Map<String, String> addMoney(String token, Double amount) {
        Map<String, String> forReturn = new HashMap<>();
        User user = getMe(token);
        if (user == null) {
            forReturn.put("message", "user not found");
            return forReturn;
        }
        Double currentAmount = user.getAmountBalance();
        user.setAmountBalance(amount + currentAmount);
        user = userRepository.save(user);

        forReturn.put("userName", user.getUsername());
        forReturn.put("email", user.getEmail());
        forReturn.put("amountBalance", String.valueOf(user.getAmountBalance()));
        forReturn.put("message", "ammount add successfully");
        return forReturn;
    }
//    public Map<String, String> withdraw(String token, Double amount) {
//        Map<String , String> forReturn = new HashMap<>();
//        User user = getMe(token);
//        if (user == null) {
//            forReturn.put("message" , "user not found");
//            return forReturn;
//        }
//        Double currentAmount = user.getAmountBalance();
//        if(currentAmount < amount){
//            forReturn.put("message", "not enough balance to withdraw");
//            return forReturn;
//
//        }
//        user.setAmountBalance(amount + currentAmount);
//        user = userRepository.save(user);
//
//        forReturn.put("userName", user.getUsername());
//        forReturn.put("email", user.getEmail());
//        forReturn.put("amountBalance", String.valueOf(user.getAmountBalance()));
//        forReturn.put("message", "money withdraw successfully");
//        return forReturn;
//    }

    public List<Ticket> query(String scheduleId) {
        return ticketRepository.findByScheduleId(scheduleId);
    }
//
//    public ResponseEntity<?> queryForSeat(String token, String scheduleId, String seatNumber) {
//        User user = getMe(token);
//        Ticket ticket = ticketRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber);
//        if (ticket == null) return ResponseEntity.ok("Ticket not found");
//        if (ticket.isBooked()) {
//            if (ticket.getUserId().equals(user.getId())) return ResponseEntity.ok(ticket);
//            else return ResponseEntity.ok("Ticket is booked by another user");
//
//        }
//        return ResponseEntity.ok(ticket);
//    }

    public ResponseEntity<?> book(String token, String scheduleId, String seatNumber, String paymentId) {
        User user = getMe(token);
        Ticket ticket = ticketRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber);
        if (ticket == null) return ResponseEntity.ok("Ticket not found");
        if (ticket.isBooked()) {
            if (ticket.getUserId().equals(user.getId())) return ResponseEntity.ok(ticket);
            else return ResponseEntity.ok("Ticket is booked by another user");

        }
        if (user.getAmountBalance() < ticket.getPrice()) return ResponseEntity.ok("Not enough money");

        user.setAmountBalance(user.getAmountBalance() - ticket.getPrice());
        user = userRepository.save(user);
        ticket.setBooked(true);
        ticket.setUserId(user.getId());
        if (paymentId == null) {
            return ResponseEntity.ok("Payment is not done");
        }
        var schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isEmpty()) {
            return ResponseEntity.ok("Schedule not found");
        }
        var owner = getMovieDirectorAndTheaterOwner(scheduleId);
        if (owner == null) return ResponseEntity.ok("Owner not found");
        Tax tax = getTax(owner, ticket, schedule.get());
        tax = taxRepository.save(tax);
        ticket.setPaymentId(paymentId);
        ticket = ticketRepository.save(ticket);
        return ResponseEntity.ok(ticket);
    }

    private Tax getTax(Pair<User, User> owner, Ticket ticket, Schedule schedule) {
        var distributor = owner.getLeft();
        var theaterOwner = owner.getRight();
        double distributorPercentage = 0.5;
        var distributorAmount = ticket.getPrice() * distributorPercentage + distributor.getAmountBalance();
        double theaterOwnerPercentage = 0.3;
        var theaterOwnerAmount = ticket.getPrice() * theaterOwnerPercentage + theaterOwner.getAmountBalance();
        double taxPercentage = 0.2;
        var taxAmount = ticket.getPrice() * taxPercentage;
        distributor.setAmountBalance(distributorAmount);
        theaterOwner.setAmountBalance(theaterOwnerAmount);
        distributor = userRepository.save(distributor);
        theaterOwner = userRepository.save(theaterOwner);
        Tax tax = new Tax();
        tax.setTheaterId(schedule.getTheaterId());
        tax.setMovieName(schedule.getMovieName());
        tax.setTax(taxAmount);
        return tax;
    }

    public ResponseEntity<?> myTickets(String token) {

        User user = getMe(token);
        if (user == null) return ResponseEntity.ok("User not found");
        List<Ticket> tickets = ticketRepository.findAllByUserId(user.getId());
        return ResponseEntity.ok(tickets);
    }

    Pair<User, User> getMovieDirectorAndTheaterOwner(String scheduleId) {
        if (scheduleId == null) return null;
        var schedule = scheduleRepository.findById(scheduleId);
        if (schedule.isEmpty()) return null;
        var movieName = schedule.get().getMovieName();
        var movie = movieRepository.findByMovieName(movieName);
        if (movie.isEmpty()) return null;
        var distributorId = movie.get().getDistributorId();
        var distributor = userRepository.findById(distributorId);
        if (distributor.isEmpty()) return null;


        var theaterId = schedule.get().getTheaterId();
        var theater = theaterRepository.findById(theaterId);
        if (theater.isEmpty()) return null;

        var licenseId = theater.get().getLicenseId();
        var license = licenseRepository.findLicenseById(licenseId);
        if (license.isEmpty()) return null;
        var theaterOwnerId = license.get().getLicenseOwner();
        var theaterOwner = userRepository.findById(theaterOwnerId);
        return theaterOwner.map(user -> Pair.of(distributor.get(), user)).orElse(null);

    }

    public ResponseEntity<?> bookMultiple(String token, String scheduleId, String[] seatNumbers, String paymentId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) return ResponseEntity.ok("Schedule not found");
        List<Ticket> tickets = getSeat(scheduleId, seatNumbers);
        if (tickets == null) return ResponseEntity.ok("Seat not found");
        User user = getMe(token);
        if (user == null) return ResponseEntity.ok("User not found");
        double totalTicketPrice = 0.0;
        for (Ticket ticket : tickets) {
            totalTicketPrice += ticket.getPrice();
        }
        if (user.getAmountBalance() < totalTicketPrice) return ResponseEntity.ok("Not enough money");
        user.setAmountBalance(user.getAmountBalance() - totalTicketPrice);
        user = userRepository.save(user);
        for (Ticket ticket : tickets) {
            ticket.setBooked(true);
            ticket.setUserId(user.getId());
            ticket.setPaymentId(paymentId);
            ticketRepository.save(ticket);
        }
        var owner = getMovieDirectorAndTheaterOwner(scheduleId);
        if (owner == null) return ResponseEntity.ok("Owner not found");
        for (Ticket ticket : tickets) {
            Tax tax = getTax(owner, ticket, schedule);
            taxRepository.save(tax);
        }
        return ResponseEntity.ok(tickets);


    }

    List<Ticket> getSeat(String scheduleId, String[] seatNumber) {
        List<Ticket> tickets = new LinkedList<>();
        for (String s : seatNumber) {
            Ticket ticket = ticketRepository.findByScheduleIdAndSeatNumber(scheduleId, s);
            System.out.println(ticket + " " + s);
            if (ticket == null) return null;
            if (ticket.isBooked()) return null;
            tickets.add(ticket);
        }
        return tickets;

    }

    public ResponseEntity<?> addReview(String token, ReviewRequest review) {
        User user = getMe(token);
        if (user == null) return ResponseEntity.badRequest().body("User not found");
        var schedule = scheduleRepository.findById(review.getScheduleId());
        if (schedule.isEmpty()) return ResponseEntity.badRequest().body("Invalid ticket");
        var movie = movieRepository.findByMovieName(schedule.get().getMovieName());
        if (movie.isEmpty()) return ResponseEntity.badRequest().body("Movie not found");
        var theaterId = schedule.get().getTheaterId();
        if (theaterId == null) return ResponseEntity.badRequest().body("Theater not found");

        Review newReview = new Review();
        newReview.setMovieId(movie.get().getId());
        newReview.setTheatreId(theaterId);
        newReview.setUserId(user.getId());
        newReview.setOpinion(review.getOpinion());
        newReview.setSentimentScore(getSentimentScoreWithTry(review.getOpinion()));
        newReview = reviewRepository.save(newReview);
        return ResponseEntity.ok(newReview);


    }

    public Double getSentimentScore(String opinion) throws IOException {
        URL url = new URL("http://localhost:5000");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        String jsonInputString = "{\"opinion\": \"" + opinion + "\"}";
        System.out.println(jsonInputString);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(jsonInputString);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());

        return get_double_from_string(response.toString());

    }

    double get_double_from_string(String s) {
        s = s.substring(1, s.length() - 2);
        return Double.parseDouble(s);
    }

    Double getSentimentScoreWithTry(String opinion) {
        try {
            return getSentimentScore(opinion);
        } catch (IOException e) {
            return (double) 0;
        }
    }

//    public ResponseEntity<?> addReviewForReleasedMovie(Review review) {
//        String opinion = review.getOpinion();
//
//        // need to replace all the special characters and new line
//        opinion = opinion.replaceAll("[^a-zA-Z0-9 ]", "");
//        review.setOpinion(opinion);
//        review.setSentimentScore(getSentimentScoreWithTry(opinion));
//        review = reviewRepository.save(review);
//        return ResponseEntity.ok(review);
//    }

    public ResponseEntity<?> addReviewForMovieName(String token, String movieName, ReviewRequest review) {
        User user = getMe(token);
        System.out.println(user);
        if (user == null) return ResponseEntity.badRequest().body("user not found");
        var movie = movieRepository.findByMovieName(movieName);
        if (movie.isEmpty()) return ResponseEntity.badRequest().body("Movie not found");
        List<Ticket> tickets = ticketRepository.findAllByUserId(user.getId());
        System.out.println(tickets);
        if(tickets.isEmpty()){
            return ResponseEntity.badRequest().body("User have not seen the movie");

        }
        Set<String> scheduleIds = new HashSet<>();
        for (Ticket ticket : tickets) {
            scheduleIds.add(ticket.getScheduleId());
        }
        System.out.println(scheduleIds);
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieName(movieName);
//        Schedule schedule = findAvailableSchedule(scheduleList, scheduleIds);
        boolean haveSeen = false;
        for(String scheduleId : scheduleIds){
            Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
            if(schedule.isPresent()){
                if(Objects.equals(schedule.get().getMovieName(), movieName)){
                    haveSeen = true;
                    review.setScheduleId(scheduleId);
                    break;
                }
            }
        }
        if(!haveSeen){
            return ResponseEntity.badRequest().body("User have not seen the movie");

        }

        return addReview(token, review);

    }

    Schedule findAvailableSchedule(List<Schedule> scheduleList, Set<String> scheduleIds) {
        for (Schedule schedule : scheduleList) {
            if (!scheduleIds.contains(schedule.getScheduleId())) {
                return schedule;
            }
        }
        return null;
    }
}

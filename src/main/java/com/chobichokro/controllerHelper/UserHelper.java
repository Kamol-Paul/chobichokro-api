package com.chobichokro.controllerHelper;

import com.chobichokro.models.Schedule;
import com.chobichokro.models.Tax;
import com.chobichokro.models.Ticket;
import com.chobichokro.models.User;
import com.chobichokro.relationRepository.TheaterMoviePendingRepository;
import com.chobichokro.relationRepository.TheaterMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterNewMovieRelationRepository;
import com.chobichokro.relationRepository.TheaterOwnerMovieRelationRepository;
import com.chobichokro.repository.*;
import com.chobichokro.security.jwt.JwtUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserHelper {
    private final double distributorPercentage = 0.5;
    private final double theaterOwnerPercentage = 0.3;
    private final double taxPercentage = 0.2;
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
    private TaxRepository taxRepository;

    public User getMe(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token.substring(7));
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);

    }

    public Pair<User, String> setAmount(String token, Double amount) {
        User user = getMe(token);
        if (user == null) return null;
        user.setAmountBalance(amount);
        user = userRepository.save(user);
        return Pair.of(user, "successfully set amount balance");
    }

    public Object getAllSchedule() {
        return scheduleRepository.findAll();
    }

    public Pair<User, String> addMoney(String token, Double amount) {
        User user = getMe(token);
        if (user == null) return null;
        Double currentAmount = user.getAmountBalance();
        user.setAmountBalance(amount + currentAmount);
        user = userRepository.save(user);
        return Pair.of(user, "successfully add amount balance");
    }

    public List<Ticket> query(String scheduleId) {
        return ticketRepository.findByScheduleId(scheduleId);
    }

    public ResponseEntity<?> queryForSeat(String token, String scheduleId, String seatNumber) {
        User user = getMe(token);
        Ticket ticket = ticketRepository.findByScheduleIdAndSeatNumber(scheduleId, seatNumber);
        if (ticket == null) return ResponseEntity.ok("Ticket not found");
        if (ticket.isBooked()) {
            if (ticket.getUserId().equals(user.getId())) return ResponseEntity.ok(ticket);
            else return ResponseEntity.ok("Ticket is booked by another user");

        }
        return ResponseEntity.ok(ticket);
    }

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
        var distributorAmount = ticket.getPrice() * distributorPercentage + distributor.getAmountBalance();
        var theaterOwnerAmount = ticket.getPrice() * theaterOwnerPercentage + theaterOwner.getAmountBalance();
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

}

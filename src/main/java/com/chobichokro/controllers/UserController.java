package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.UserHelper;
import com.chobichokro.payload.request.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserHelper userHelper;

    @GetMapping("/get_me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userHelper.getMe(token));
    }

//    @PostMapping("/set_amount")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public ResponseEntity<?> setAmount(@RequestHeader("Authorization") String token, @RequestParam("amount") Double amount) {
//        return ResponseEntity.ok(userHelper.setAmount(token, amount));
//    }

    @GetMapping("/all_schedule")
    public ResponseEntity<?> getAllSchedule() {
        return ResponseEntity.ok(userHelper.getAllSchedule());
    }

//    @PostMapping("add_money")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_DISTRIBUTOR')")
//    public ResponseEntity<?> addMoney(@RequestHeader("Authorization") String token, @RequestParam("amount") Double amount) {
//        return ResponseEntity.ok(userHelper.addMoney(token, amount));
//    }
//    @PostMapping("withdraw_money")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_DISTRIBUTOR')")
//    public ResponseEntity<?> withdrawMoney(@RequestHeader("Authorization") String token, @RequestParam("amount") Double amount) {
//        return ResponseEntity.ok(userHelper.withdraw(token,amount));
//    }

//    @GetMapping("/query/{scheduleId}")
//    public ResponseEntity<?> query(@PathVariable("scheduleId") String scheduleId) {
//        return ResponseEntity.ok(userHelper.query(scheduleId));
//    }

//    @GetMapping("/query/{scheduleId}/{seatNumber}")
//    public ResponseEntity<?> query(@RequestHeader("Authorization") String token, @PathVariable("scheduleId") String scheduleId, @PathVariable("seatNumber") String seatNumber) {
//        return userHelper.queryForSeat(token, scheduleId, seatNumber);
//    }

//    @PostMapping("/book/{scheduleId}/{seatNumber}")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER')")
//    public ResponseEntity<?> book(@RequestHeader("Authorization") String token, @PathVariable("scheduleId") String scheduleId, @PathVariable("seatNumber") String seatNumber, @RequestParam("paymentId") String paymentId) {
//        return userHelper.book(token, scheduleId, seatNumber, paymentId);
//    }

    @GetMapping("/my_tickets")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> myTickets(@RequestHeader("Authorization") String token) {
        return userHelper.myTickets(token);
    }

    @PostMapping("/book_multiple/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER')")
    public ResponseEntity<?> bookMultiple(@RequestHeader("Authorization") String token, @PathVariable("scheduleId") String scheduleId, @RequestParam("paymentId") String paymentId, @RequestParam("seatNumbers") String[] seatNumbers) {
        System.out.println(Arrays.toString(seatNumbers));
        System.out.println(paymentId);
        return userHelper.bookMultiple(token, scheduleId, seatNumbers, paymentId);
    }

    //    @PostMapping("/add_review")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String token, @ModelAttribute ReviewRequest review) {
//        return userHelper.addReview(token , review);
//    }
//    @GetMapping("/")
//    public ResponseEntity<?> score() throws IOException, InterruptedException {
//        return ResponseEntity.ok(userHelper.getSentimentScore("I hate this movie"));
//    }
//    @PostMapping("/add_released_movie_review")
//    public ResponseEntity<?> addReviewForReleasedMovie(@ModelAttribute Review review) {
//        return userHelper.addReviewForReleasedMovie(review);
//    }
    @PostMapping("/add_review/{movieName}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addReviewForReleasedMovie(@RequestHeader("Authorization") String token, @PathVariable("movieName") String movieName, @ModelAttribute ReviewRequest review) {
        return userHelper.addReviewForMovieName(token, movieName, review);
    }

}

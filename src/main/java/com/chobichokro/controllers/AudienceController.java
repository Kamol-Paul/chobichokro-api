package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.AudienceHelper;
import com.chobichokro.controllerHelper.UserHelper;
import com.chobichokro.payload.request.ReviewRequest;
import com.chobichokro.payload.request.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Arrays;

@RestController
@RequestMapping("/api/audience")
public class AudienceController {
    @Autowired
    AudienceHelper audienceHelper;
    @Autowired
    UserHelper userHelper;

    @GetMapping("/get_theater_list")
    ResponseEntity<?> getTheaterList(@ModelAttribute ScheduleRequest scheduleRequest) throws ParseException {
        System.out.println(scheduleRequest);
        return ResponseEntity.ok(audienceHelper.getTheaterlist(scheduleRequest.getMovieName()));
    }

    @GetMapping("/get_showtime_list")
    ResponseEntity<?> getScheduleList(@ModelAttribute ScheduleRequest scheduleRequest) throws ParseException {
        System.out.println(scheduleRequest);
        return ResponseEntity.ok(audienceHelper.getScheduleList(scheduleRequest.getMovieName(), scheduleRequest.getTheaterId()));
    }

    @GetMapping("/get_hall_list")
    ResponseEntity<?> getHallNumberList(@ModelAttribute ScheduleRequest scheduleRequest) {
        System.out.println(scheduleRequest);
        return ResponseEntity.ok(audienceHelper.getHallNumberList(scheduleRequest.getMovieName(), scheduleRequest.getTheaterId(), scheduleRequest.getDate()));
    }

    @GetMapping("/get_schedule_id")
    ResponseEntity<?> getScheduleId(@ModelAttribute ScheduleRequest scheduleRequest) {
        System.out.println(scheduleRequest);
        return ResponseEntity.ok(audienceHelper.getScheduleId(scheduleRequest.getMovieName(), scheduleRequest.getTheaterId(), scheduleRequest.getDate(), scheduleRequest.getHallNumber()));
    }

    @GetMapping("/get_ticket_list/{scheduleId}")
    ResponseEntity<?> getTicketList(@PathVariable("scheduleId") String scheduleId) {
        System.out.println(scheduleId);
        return ResponseEntity.ok(audienceHelper.getTicketList(scheduleId));
    }

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

    @PostMapping("/add_review")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String token, @ModelAttribute ReviewRequest review) {
        return userHelper.addReview(token, review);
    }

//    @PostMapping("add_money")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_DISTRIBUTOR')")
//    public ResponseEntity<?> addMoney(@RequestHeader("Authorization") String token, @RequestParam("amount") Double amount) {
//        return ResponseEntity.ok(userHelper.addMoney(token, amount));
//    }
//
//    @PostMapping("withdraw_money")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_THEATER_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_DISTRIBUTOR')")
//    public ResponseEntity<?> withdrawMoney(@RequestHeader("Authorization") String token, @RequestParam("amount") Double amount) {
//        return ResponseEntity.ok(userHelper.withdraw(token,amount));
//    }

}

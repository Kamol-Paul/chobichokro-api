package com.chobichokro.controllers;


import com.chobichokro.controllerHelper.TicketHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    private TicketHelper ticketHelper;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/all_available")
    public ResponseEntity<?> getAllAvailableTickets() {
        return ticketHelper.getAllAvailableTickets();
    }
}

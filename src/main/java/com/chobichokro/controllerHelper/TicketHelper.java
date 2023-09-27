package com.chobichokro.controllerHelper;

import com.chobichokro.models.Ticket;
import com.chobichokro.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketHelper {
    //    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private TheaterRepository theaterRepository;
//    @Autowired
    private TicketRepository ticketRepository;
//    @Autowired
//    private MovieRepository movieRepository;
//    @Autowired
//    private ScheduleRepository scheduleRepository;
//    @Autowired
//    private TheaterMovieRelationRepository theaterMovieRelationRepository;

//    @Autowired
//    private TheaterNewMovieRelationRepository theaterNewMovieRelationRepository;
//    @Autowired
//    private LicenseRepository licenseRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private JwtUtils jwtUtils;
//    @Autowired
//    private TheaterMoviePendingRepository theaterMoviePendingRepository;
//    @Autowired
//    private TheaterOwnerMovieRelationRepository theaterOwnerMovieRelationRepository;

//    public ResponseEntity<?> getAllAvailableTickets() {
//        List<Ticket> allAvailableTickets = ticketRepository.findAll();
//        allAvailableTickets.removeIf(Ticket::isBooked);
//        return ResponseEntity.ok(allAvailableTickets);
//    }

    public ResponseEntity<?> getTicketsByScheduleId(String scheduleId) {
        List<Ticket> tickets = ticketRepository.findAllByScheduleId(scheduleId);
        return ResponseEntity.ok(tickets);
    }
}

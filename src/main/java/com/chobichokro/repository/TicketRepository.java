package com.chobichokro.repository;

import com.chobichokro.models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    @Override
    Optional<Ticket> findById(String string);

    List<Ticket> findAllByScheduleId(String scheduleId);

    List<Ticket> findAllByUserId(String userId);

    public default Ticket bookTicket(String ticketId, String userId, String paymentId) {
        Ticket ticket = findById(ticketId).get();
        ticket.setBooked(true);
        ticket.setUserId(userId);
        ticket.setPaymentId(paymentId);
        return ticket;
    }


    List<Ticket> findByScheduleId(String scheduleId);

    Ticket findByScheduleIdAndSeatNumber(String scheduleId, String seatNumber);
}

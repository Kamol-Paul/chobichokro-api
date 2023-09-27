package com.chobichokro.controllerHelper;

import com.chobichokro.models.Schedule;
import com.chobichokro.models.Theater;
import com.chobichokro.models.Ticket;
import com.chobichokro.repository.ScheduleRepository;
import com.chobichokro.repository.TheaterRepository;
import com.chobichokro.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AudienceHelper {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    TheaterRepository theaterRepository;
    @Autowired
    TicketRepository ticketRepository;

    public List<Theater> getTheaterlist(String movieName) {
        List<Schedule> scheduleList = scheduleRepository.findByMovieName(movieName);
        Set<String> theaterIdList = scheduleList.stream().map(Schedule::getTheaterId).collect(Collectors.toSet());
        return theaterRepository.findAllById(theaterIdList);
    }


    public Object getScheduleList(String movieName, String theaterId) {
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieNameAndTheaterId(movieName, theaterId);
        return scheduleList.stream().map(Schedule::getScheduleDate).collect(Collectors.toSet());
    }

    public Object getHallNumberList(String movieName, String theaterId, String showTime) {
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieNameAndTheaterIdAndScheduleDate(movieName, theaterId, showTime);
        return scheduleList.stream().map(Schedule::getHallNumber).collect(Collectors.toSet());
    }

    public Object getScheduleId(String movieName, String theaterId, String date, int hallNumber) {
        List<Schedule> scheduleList = scheduleRepository.findAllByMovieNameAndTheaterIdAndScheduleDateAndAndHallNumber(movieName, theaterId, date, hallNumber);
        if (scheduleList.isEmpty()) {
            return "No Schedule Found";
        } else {
            return scheduleList.get(0).getScheduleId();
        }
    }

    public Object getTicketList(String scheduleId) {
        List<Ticket> tickets = ticketRepository.findAllByScheduleId(scheduleId);
        List<String> available_seat = new ArrayList<>();
        List<String> booked_seat = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.isBooked()) {
                booked_seat.add(ticket.getSeatNumber());
            } else {
                available_seat.add(ticket.getSeatNumber());
            }
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("available_seat", available_seat);
        map.put("booked_seat", booked_seat);
        return map;
    }
}

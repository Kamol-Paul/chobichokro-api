package com.chobichokro.controllerHelper;

import com.chobichokro.models.*;
import com.chobichokro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Autowired
    LicenseRepository licenseRepository;
    @Autowired
    TheaterHelper theaterHelper;
    @Autowired
    UserRepository userRepository;

    public List<Theater> getTheaterlist(String movieName) throws ParseException {
        List<Schedule> scheduleList = scheduleRepository.findByMovieName(movieName);
//        Set<String> theaterIdList = scheduleList.stream().map(Schedule::getTheaterId).collect(Collectors.toSet());
        Set<String> theaterIdList = new HashSet<>();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        System.out.println(currentDate);
        for(Schedule schedule : scheduleList){
            Date scheduleDate  = dateFormat.parse(schedule.getScheduleDate());
            System.out.println(scheduleDate.toString() + schedule);
            if(currentDate.before(scheduleDate)) theaterIdList.add(schedule.getTheaterId());

        }
        List<Theater> theaters = theaterRepository.findAllById(theaterIdList);
        for(Theater theater : theaters){
            String theaterOwnerId = getTheaterOwnerId(theater);
            theater.setId(theaterOwnerId);
        }
        return theaters;


    }

    public String getTheaterOwnerId(Theater theater){
        String licenseId = theater.getLicenseId();
        Optional<License> license = licenseRepository.findById(licenseId);
        System.out.println(license.get());
        Optional<User> user = userRepository.findById(license.get().getLicenseOwner());
        System.out.println(user.get());
        return user.get().getId();

    }

    public Object getScheduleList(String movieName, String theaterId) throws ParseException {
        Theater theater = theaterHelper.getTheaterFromTheaterOwner(theaterId);
        System.out.println(theater);

        List<Schedule> scheduleList = scheduleRepository.findAllByMovieNameAndTheaterId(movieName, theater.getId());
        System.out.println(scheduleList);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        System.out.println(currentDate);
        Set<String> showTime = new HashSet<>();
        for(Schedule schedule : scheduleList){
            Date scheduleDate  = dateFormat.parse(schedule.getScheduleDate());
            System.out.println(scheduleDate.toString() + schedule);
            if(currentDate.before(scheduleDate)) showTime.add(schedule.getScheduleDate());

        }
        return showTime;
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

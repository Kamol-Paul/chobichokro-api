package com.chobichokro.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String ticketId;
    private boolean isBooked;
    private String scheduleId;
    private String seatNumber;
    private String userId;
    private String paymentId;
    private int price;

    public Ticket(String scheduleId, String seatNumber, int price) {
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public List<Ticket> getTicketForSchedule(String scheduleId, int price) {
        List<Ticket> tickets = new LinkedList<>();
        for(char ch = 'A'; ch <= 'G'; ch++) {
            for(int i = 1; i < 10; i++){
                String str = ch + '0' + String.valueOf(i);
                tickets.add(new Ticket(scheduleId, str, price));
            }
            for(int i = 10; i <= 20; i++) {
                String str = ch + String.valueOf(i);
                tickets.add(new Ticket(scheduleId, str, price));
            }
        }
        return tickets;

    }

}

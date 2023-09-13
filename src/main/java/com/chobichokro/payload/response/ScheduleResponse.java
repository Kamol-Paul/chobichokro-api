package com.chobichokro.payload.response;

import com.chobichokro.models.Schedule;
import com.chobichokro.models.Ticket;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleResponse {
    private Schedule schedule;
    private List<Ticket> tickets;
    private String message;
}

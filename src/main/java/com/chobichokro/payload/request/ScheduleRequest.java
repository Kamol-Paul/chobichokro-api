package com.chobichokro.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequest {
    String movieName;
    String theaterId;
    String date;
    int hallNumber;
}

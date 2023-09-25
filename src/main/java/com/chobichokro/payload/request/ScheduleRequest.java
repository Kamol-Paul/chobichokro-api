package com.chobichokro.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScheduleRequest {
    String movieName;
    String theaterId;
    String date;
    int hallNumber;
}

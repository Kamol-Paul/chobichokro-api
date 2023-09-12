package com.chobichokro.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "schedules")
public class Schedule {
    @Id
    private String scheduleId;
    @NotBlank
    private String movieName;
    @NotBlank
    private String TheaterId;
    @NotBlank
    private Date scheduleDate;
    @NotBlank
    private int hallNumber;
}

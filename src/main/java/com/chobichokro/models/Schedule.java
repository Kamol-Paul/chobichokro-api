package com.chobichokro.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String theaterId;
    @NotBlank
    private String scheduleDate;
    @NotBlank
    private int hallNumber;
}

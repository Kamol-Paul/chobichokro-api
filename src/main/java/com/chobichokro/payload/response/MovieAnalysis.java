package com.chobichokro.payload.response;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Review;
import com.chobichokro.models.Theater;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieAnalysis {
    private Movie movie;
    private int totalTicket;
    private double totalRevenue;
    private int totalScreening;
    private int totalTheater;
    private double averageSentiment;
    private String movieVerdict;
    List<Review> reviews;
    List<Theater> theaters;
}

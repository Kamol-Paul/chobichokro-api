package com.chobichokro.payload.response;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Review;
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
    private int totalRevenue;
    private int totalScreening;
    private int totalTheater;
    private double averageSentiment;
    private String movieVerdict;
    List<Review> reviews;
}

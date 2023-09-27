package com.chobichokro.payload.response;

import com.chobichokro.models.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MyMovieResponse {
    private Movie movie;
    private String theaterOwnerId;
    private String distributorName;
}

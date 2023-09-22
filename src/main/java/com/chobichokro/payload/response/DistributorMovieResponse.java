package com.chobichokro.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DistributorMovieResponse {

    private String id;
    private String movieName;

    private String[] genre;
    private String[] cast;
    private String[] director;

    private Date releaseDate;
    private String trailerLink;
    private String posterImageLink;
    private String status;
    private String description;
    private String distributorId;
    private  String distributorName;


}

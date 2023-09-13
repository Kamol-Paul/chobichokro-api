package com.chobichokro.relation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "TheaterNewMovieRelation")
public class TheaterNewMovieRelation {
    /*
    the relation will store the theater owner id and the new movie id that he/she can to add
    */
    @Id
    private String id;
    private String theaterOwnerId;
    private String newMovieId;

    public TheaterNewMovieRelation(String theaterOwnerId, String movieId) {
        this.theaterOwnerId = theaterOwnerId;
        this.newMovieId = movieId;
    }

    public void setMovieId(String movieId) {
        this.newMovieId = movieId;
    }

    public void setTheaterId(String licenseId) {
        this.theaterOwnerId = licenseId;
    }
}

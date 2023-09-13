package com.chobichokro.relation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "TheaterOwnMovieRelation")
public class TheaterOwnMovieRelation {
    /*
    * The class is used to store the relation between theater owner and movie that he/she owns
     */
    @Id
    private String id;
    private String theaterOwnerId;
    private String movieId;
}

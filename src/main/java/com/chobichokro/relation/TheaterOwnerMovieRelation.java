package com.chobichokro.relation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "TheaterOwnerMovieRelation")
public class TheaterOwnerMovieRelation {

    @Id
    private String id;
    private String theaterOwnerId;
    private String movieId;


}

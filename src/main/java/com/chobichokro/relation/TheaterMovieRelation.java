package com.chobichokro.relation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "TheaterMovieRelation")
public class TheaterMovieRelation {
    /*
     * The class is used to store the relation between theater and movie
     * That is, which theater has the access of which movie
     */
    @Id
    private String id;
    private String theaterId;
    private String movieId;


}

package com.chobichokro.relation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "TheaterMoviePending")
public class TheaterMoviePending {
    @Id
    private String id;
    private String theaterOwnerId;
    private String movieId;

}

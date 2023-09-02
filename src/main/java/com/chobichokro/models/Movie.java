package com.chobichokro.models;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "movies")
public class Movie {
    @Id
    @NotBlank
    private String id;
    @NotBlank
    private String movieName;
//    private String description;
    @NotBlank
    private String[] genre;
    @NotBlank
    private String[] cast;
    private String[] director;

    private Date releaseDate;
    @NotBlank
    private String trailerLink;
    private String posterImageLink;

}

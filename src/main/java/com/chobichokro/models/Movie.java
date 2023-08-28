package com.chobichokro.models;


import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "movies")
public class Movie {
    @Id
    private String id;
    @NotBlank
    private String title;
    private String description;
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

package com.chobichokro.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
//    @?
    private String id;
//    @NotBlank
    private String movieName;
    //    private String description;
//    @NotBlank
    private String[] genre;
//    @NotBlank
    private String[] cast;
    private String[] director;

    private Date releaseDate;
//    @NotBlank
    private String trailerLink;
    private String posterImageLink;
    private String message;

    public MovieResponse(String message) {
        this.message = message;
    }
}

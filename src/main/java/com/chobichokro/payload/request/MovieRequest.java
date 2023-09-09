package com.chobichokro.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieRequest {

//    @NotBlank
    private String movieName;
    //    private String description;
//    @NotBlank
    private String[] genre;
//    @NotBlank
    private String[] cast;
    private String[] director;

    private String releaseDate;
    @NotBlank
    private String trailerLink;
    private MultipartFile image;
    private String status;
}

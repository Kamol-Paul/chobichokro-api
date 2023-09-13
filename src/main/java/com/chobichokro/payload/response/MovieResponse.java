package com.chobichokro.payload.response;

import com.chobichokro.models.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    private InputStream image;
    private String status;
    private String description;
    private String distributorId;
    private List<String> theaterOwnerToSend;

    public MovieResponse(String message) {
        this.message = message;
    }
}

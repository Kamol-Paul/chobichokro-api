package com.chobichokro.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "theaters")
public class Theater {
    @Id
    private String id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    private String address;
    private  int numberOfScreens;
//    private int[] numberOfSeats;
    @NotBlank
    private String licenseId;


}

package com.chobichokro.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@ToString
public class TheaterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    private String address;

    private String contactNumber;
}

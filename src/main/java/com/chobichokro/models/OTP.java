package com.chobichokro.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OTP {
    @Id
    private String id;
    private String email;
    private String otp;
}

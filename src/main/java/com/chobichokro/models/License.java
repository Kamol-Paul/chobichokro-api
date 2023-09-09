package com.chobichokro.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "licenses")
public class License {
    @Id
    @NotBlank
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String email;

    @NotBlank
    private String transactionNumber;
    @NotBlank
    private String address;
    @NotBlank
    private String status;
    @NotBlank
    private String licenseType;
    @NotBlank
    private String licenseOwner;

    private String licenseNumber;
    @NotBlank
    private String verificationCode;
}

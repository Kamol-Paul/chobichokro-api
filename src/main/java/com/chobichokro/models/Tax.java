package com.chobichokro.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "taxes")
public class Tax {
    @Id
    private String id;
    private String movieName;
    private String theaterId;
    private Double tax;
}

package com.chobichokro.payload.response;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Theater;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PendingResponses {
    private String id;
    private Movie movie;
    private Theater theater;

}

package com.chobichokro.payload.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DirectorAnalysis {
    List<MovieAnalysis> movieAnalysisList;
    private String directorName;
    private int totalMovie;
}

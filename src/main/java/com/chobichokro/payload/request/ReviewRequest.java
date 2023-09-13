package com.chobichokro.payload.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewRequest {
    private String scheduleId;
    private String opinion;
}

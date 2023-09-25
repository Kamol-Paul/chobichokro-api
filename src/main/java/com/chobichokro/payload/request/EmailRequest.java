package com.chobichokro.payload.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmailRequest {
    private String recipient;
    private String msgBody;
    private String subject;
}

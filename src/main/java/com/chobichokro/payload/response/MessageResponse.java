package com.chobichokro.payload.response;

import lombok.Getter;

@Getter
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

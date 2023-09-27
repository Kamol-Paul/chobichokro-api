package com.chobichokro.payload.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final List<String> roles;
    private String id;
    private String username;
    private String email;

    public JwtResponse(String accessToken, String id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setUsername(String username) {
        this.username = username;
    }

}

package com.chobichokro.services;

import com.chobichokro.payload.request.EmailRequest;

public interface EmailService {
    String sendSimpleMail(EmailRequest details);

}

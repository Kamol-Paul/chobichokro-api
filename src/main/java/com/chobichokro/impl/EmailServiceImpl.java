package com.chobichokro.impl;

import com.chobichokro.payload.request.EmailRequest;
import com.chobichokro.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public String sendSimpleMail(EmailRequest details) {
        System.out.println(details);
        try {

            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return "success";
        }

        catch (Exception e) {
            System.out.println("hi");
            System.out.println(e.getLocalizedMessage());
            System.out.println("hello");
            return "Error while Sending Mail";
        }
    }
}

package com.chobichokro.controllers;

import com.chobichokro.models.OTP;
import com.chobichokro.payload.request.EmailRequest;
import com.chobichokro.payload.request.SignupRequest;
import com.chobichokro.repository.OTPRepository;
import com.chobichokro.repository.UserRepository;
import com.chobichokro.services.EmailService;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    OTPRepository otpRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;
    @PostMapping("/send")
    public ResponseEntity<?> sendMail(@ModelAttribute EmailRequest emailRequest){
        String status = emailService.sendSimpleMail(emailRequest);
        return ResponseEntity.ok(status);
    }
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@ModelAttribute SignupRequest signupRequest){

//        if(userRepository.existsByUsername(signupRequest.getUsername())){
//            ResponseEntity.ok("Username is already taken");
//        }
//        if(userRepository.existsByEmail(signupRequest.getEmail())){
//            ResponseEntity.ok("Email is already taken");
//        }
        Random random = new Random();
        int otp = random.nextInt(9999);
        String otpString = String.format("%04d", otp);
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipient(signupRequest.getEmail());
        emailRequest.setSubject("OTP for Chobi Chokro");
        emailRequest.setMsgBody("Your OTP is: " + otpString);
        String status = emailService.sendSimpleMail(emailRequest);
        if(status.equals("success")){
            OTP otpObject = new OTP();
            otpObject.setEmail(signupRequest.getEmail());
            otpObject.setOtp(otpString);
            // save to database
            OTP userOtp = otpRepository.findByEmail(signupRequest.getEmail());
            if(userOtp != null){
                otpObject.setId(userOtp.getId());
            }
            otpRepository.save(otpObject);
            return ResponseEntity.ok(otpString);
        }
        return ResponseEntity.ok("Failed to send OTP");
    }
    @PostMapping("/get-mail-otp/{email}")
    public ResponseEntity<?> getMailOtp(@PathVariable("email") String email){
        OTP otpObject = otpRepository.findByEmail(email);
        Random random = new Random();
        int otp = random.nextInt(9999);
        String otpString = String.format("%04d", otp);
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipient(email);
        emailRequest.setSubject("OTP for Chobi Chokro");
        emailRequest.setMsgBody("Your OTP is: " + otpString);
        String status = emailService.sendSimpleMail(emailRequest);
        OTP forReturn = new OTP();
        if(otpObject != null){
            forReturn.setId(otpObject.getId());
        }
        if(status.equals("success")){
            forReturn.setOtp(otpString);
            otpRepository.save(forReturn);
            return ResponseEntity.ok(otpString);
        }
        return ResponseEntity.ok("Failed to send OTP");

    }
}

package com.chobichokro.controllers;

import com.chobichokro.models.*;
import com.chobichokro.payload.request.LoginRequest;
import com.chobichokro.payload.request.SignupRequest;
import com.chobichokro.payload.response.JwtResponse;
import com.chobichokro.payload.response.MessageResponse;
import com.chobichokro.repository.LicenseRepository;
import com.chobichokro.repository.RoleRepository;
import com.chobichokro.repository.TheaterRepository;
import com.chobichokro.repository.UserRepository;
import com.chobichokro.security.jwt.JwtUtils;
import com.chobichokro.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    TheaterRepository theaterRepository;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@ModelAttribute LoginRequest loginRequest) {
        System.out.println("signin");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        JwtResponse jwtResponse = new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
        System.out.println(jwtResponse);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@ModelAttribute SignupRequest signUpRequest) {
        System.out.println("signup");
        System.out.println(signUpRequest.getUsername());
        System.out.println(signUpRequest.getEmail());
        System.out.println(signUpRequest.getPassword());
        System.out.println("signUpRequest " + signUpRequest);
        String licenseId = signUpRequest.getLicenseId();
        System.out.println(licenseId);

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .ok(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .ok(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        Set<String> strRoles = signUpRequest.getRoles();
        System.out.println(strRoles);
        Set<Role> roles = new HashSet<>();
        Optional<License> license = Optional.empty();
        if (licenseId != null) {
            license = licenseRepository.findByLicenseNumber(licenseId);
            System.out.println(license);

            if (license.isPresent()) {
                licenseId = license.get().getId();
                String have_role = license.get().getLicenseType();
                String status = license.get().getStatus();
                if (!Objects.equals(status, "approved")) {
                    return ResponseEntity
                            .ok(new MessageResponse("Error: Your License is not approved yet!"));
                }
                System.out.println(have_role);
                if (Objects.equals(have_role, "distributor")) {
                    System.out.println("adding distributor");
                    Role distributor = roleRepository.findByName(ERole.ROLE_DISTRIBUTOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found"));
                    roles.add(distributor);
                    System.out.println(distributor);
                } else if (Objects.equals(have_role, "theatre_owner")) {
                    System.out.println("adding theater owner");
                    Role theaterOwner = roleRepository.findByName(ERole.ROLE_THEATER_OWNER)
                            .orElseThrow(() -> new RuntimeException("Error : Role is not found."));
                    roles.add(theaterOwner);

                    Theater theater = new Theater();
                    theater.setName(signUpRequest.getUsername());
                    theater.setAddress(license.get().getAddress());
                    theater.setLicenseId(licenseId);
                    theater.setNumberOfScreens(signUpRequest.getNumberOfScreens());
                    theaterRepository.save(theater);

                }
            }
        }

        if (strRoles == null) {
            System.out.println("in null");
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            System.out.println(userRole);
        } else if (!strRoles.isEmpty()) {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    case "theatre_owner" -> {
                        Role theaterOwner = roleRepository.findByName(ERole.ROLE_THEATER_OWNER)
                                .orElseThrow(() -> new RuntimeException("Error : Role is not found."));
                        roles.add(theaterOwner);
                    }
                    case "distributor" -> {
                        Role distributor = roleRepository.findByName(ERole.ROLE_DISTRIBUTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found"));
                        roles.add(distributor);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }
        if (roles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        user.setRoles(roles);
        if (licenseId != null) {
            user.setLicenseId(licenseId);
        }
        user.setAmountBalance(10897694.0);
        user = userRepository.save(user);
        if (license.isPresent()) {
            License license1 = licenseRepository.findLicenseById(licenseId).orElse(null);
            assert license1 != null;
            license1.setLicenseOwner(user.getId());
//			licenseRepository.delete(license.get());
            licenseRepository.save(license1);


        }
        System.out.println(user);


        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

//    User authenticate(String authHeader) {
//        String token = authHeader.split(" ")[1];
//        String userName = jwtUtils.getUserNameFromJwtToken(token);
//        return userRepository.findByUsername(userName).orElse(null);
//    }
}

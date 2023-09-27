package com.chobichokro.controllers;

import com.chobichokro.models.Movie;
import com.chobichokro.models.Tax;
import com.chobichokro.models.User;
import com.chobichokro.repository.MovieRepository;
import com.chobichokro.repository.TaxRepository;
import com.chobichokro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/tax")
public class TaxController {
    @Autowired
    TaxRepository taxRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    UserRepository userRepository;

    private static Map<String, String> getStringStringMap(String key, List<Tax> value, String distributorName) {
        Map<String, String> taxAnalysis = new HashMap<>();
        taxAnalysis.put("distributorName", distributorName);
        taxAnalysis.put("movieName", key);
        int total_ticket_sell = 0;
        double total_tax_revenue = 0;
        for (Tax tax : value) {
            total_ticket_sell += 1;
            total_tax_revenue += tax.getTax();
        }
        taxAnalysis.put("ticket_sell", String.valueOf(total_ticket_sell));
        taxAnalysis.put("total_tax_revenue", String.valueOf(total_tax_revenue));
        return taxAnalysis;
    }

    @GetMapping("/")
    public ResponseEntity<?> getTaxesData() {
        List<Tax> taxes = taxRepository.findAll();
        Map<String, List<Tax>> formattedTax = new HashMap<>();
        for (Tax tax : taxes) {
            if (!formattedTax.containsKey(tax.getMovieName())) {
                formattedTax.put(tax.getMovieName(), new ArrayList<>());
            }
            List<Tax> taxList = formattedTax.get(tax.getMovieName());
            taxList.add(tax);
            formattedTax.put(tax.getMovieName(), taxList);
        }
        List<Map<String, String>> forReturn = new ArrayList<>();
        formattedTax.forEach((key, value) -> {
            Optional<Movie> movie = movieRepository.findByMovieName(key);

            if (movie.isPresent()) {
                String distributorId = movie.get().getDistributorId();
                Optional<User> distributor = userRepository.findById(distributorId);
                if (distributor.isPresent()) {
                    String distributorName = distributor.get().getUsername();
                    Map<String, String> taxAnalysis = getStringStringMap(key, value, distributorName);
                    forReturn.add(taxAnalysis);

                }

            }
        });
        return ResponseEntity.ok(forReturn);
    }
}

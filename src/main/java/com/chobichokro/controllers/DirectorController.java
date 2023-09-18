package com.chobichokro.controllers;

import com.chobichokro.controllerHelper.DirectorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/director")
public class DirectorController {
    @Autowired
    DirectorHelper directorHelper;
    @GetMapping("/get/analysis")
    public ResponseEntity<?> getAnalysis(@RequestHeader("Authorization") String token){
        return directorHelper.getDirectorAnalysis(token);
    }
}

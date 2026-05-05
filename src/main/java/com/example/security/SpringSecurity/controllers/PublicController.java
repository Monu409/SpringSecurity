package com.example.security.SpringSecurity.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PublicController {

    @GetMapping("/")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "SpringSecurity");
    }

}

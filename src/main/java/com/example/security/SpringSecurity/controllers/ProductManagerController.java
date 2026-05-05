package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.services.ProductManagerAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/product-manager")
public class ProductManagerController {

    @Autowired
    private ProductManagerAgentService agentService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Field 'message' is required"));
        }
        String response = agentService.chat(message);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
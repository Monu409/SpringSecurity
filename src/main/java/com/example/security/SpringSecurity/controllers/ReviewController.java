package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.models.ReviewModel;
import com.example.security.SpringSecurity.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewModel reviewModel) {
        return reviewService.addReview(reviewModel);
    }

    @GetMapping("/astrologer/{astrologerId}")
    public ResponseEntity<?> getAllReviews(@PathVariable String astrologerId) {
        return reviewService.getAllReviews(astrologerId);
    }
}

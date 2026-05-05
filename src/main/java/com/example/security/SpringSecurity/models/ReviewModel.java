package com.example.security.SpringSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModel {
    @Id
    private String id;
    private String astrologerId;
    private String userId;
    private String userName;
    private int rating;
    private String review;
    private LocalDateTime createdAt;
}

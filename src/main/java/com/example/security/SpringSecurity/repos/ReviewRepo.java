package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.ReviewModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepo extends MongoRepository<ReviewModel, String> {
    List<ReviewModel> findByAstrologerId(String astrologerId);
    List<ReviewModel> findTop5ByAstrologerIdOrderByRatingDescCreatedAtDesc(String astrologerId);
}

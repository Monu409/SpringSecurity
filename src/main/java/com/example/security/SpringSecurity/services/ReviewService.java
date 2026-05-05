package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.AstrologerModel;
import com.example.security.SpringSecurity.models.ReviewModel;
import com.example.security.SpringSecurity.repos.AstrologerRepo;
import com.example.security.SpringSecurity.repos.ReviewRepo;
import com.example.security.SpringSecurity.utils.CommonResDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private AstrologerRepo astrologerRepo;

    public ResponseEntity<?> addReview(ReviewModel reviewModel) {
        if (reviewModel.getRating() < 1 || reviewModel.getRating() > 5) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Rating must be between 1 and 5", null),
                HttpStatus.BAD_REQUEST
            );
        }

        Optional<AstrologerModel> optionalAstrologer;
        try {
            optionalAstrologer = astrologerRepo.findById(new ObjectId(reviewModel.getAstrologerId()));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Invalid astrologer ID format", null),
                HttpStatus.BAD_REQUEST
            );
        }

        if (optionalAstrologer.isEmpty()) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Astrologer not found", null),
                HttpStatus.NOT_FOUND
            );
        }

        reviewModel.setCreatedAt(LocalDateTime.now());
        ReviewModel savedReview = reviewRepo.save(reviewModel);

        AstrologerModel astrologer = optionalAstrologer.get();
        List<ReviewModel> allReviews = reviewRepo.findByAstrologerId(reviewModel.getAstrologerId());
        double average = allReviews.stream().mapToInt(ReviewModel::getRating).average().orElse(0.0);
        List<ReviewModel> top5 = reviewRepo.findTop5ByAstrologerIdOrderByRatingDescCreatedAtDesc(reviewModel.getAstrologerId());

        astrologer.setAverageRating(Math.round(average * 10.0) / 10.0);
        astrologer.setTopReviews(top5);
        astrologerRepo.save(astrologer);

        return new ResponseEntity<>(
            new CommonResDTO<>(true, "Review added successfully", savedReview),
            HttpStatus.OK
        );
    }

    public ResponseEntity<?> getAllReviews(String astrologerId) {
        try {
            Optional<AstrologerModel> optionalAstrologer = astrologerRepo.findById(new ObjectId(astrologerId));
            if (optionalAstrologer.isEmpty()) {
                return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Astrologer not found", null),
                    HttpStatus.NOT_FOUND
                );
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Invalid astrologer ID format", null),
                HttpStatus.BAD_REQUEST
            );
        }

        List<ReviewModel> reviews = reviewRepo.findByAstrologerId(astrologerId);
        return new ResponseEntity<>(
            new CommonResDTO<>(true, "Reviews fetched successfully", reviews),
            HttpStatus.OK
        );
    }
}

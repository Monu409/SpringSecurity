package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.CallRequestModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CallRequestRepo extends MongoRepository<CallRequestModel, String> {

    List<CallRequestModel> findByUserId(String userId);

    List<CallRequestModel> findByAstrologerId(String astrologerId);

    List<CallRequestModel> findByAstrologerIdAndStatus(String astrologerId, String status);

    List<CallRequestModel> findByUserIdAndStatus(String userId, String status);
}

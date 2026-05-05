package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.SocialModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SocialUserRepo extends MongoRepository<SocialModel, String> {
    Optional<SocialModel> findByLoginId(String loginId);
}

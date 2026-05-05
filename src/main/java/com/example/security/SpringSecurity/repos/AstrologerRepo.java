package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.AstrologerModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AstrologerRepo extends MongoRepository<AstrologerModel, ObjectId> {
    Optional<AstrologerModel> findByEmail(String email);
}

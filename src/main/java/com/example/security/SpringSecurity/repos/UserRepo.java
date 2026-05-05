package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, ObjectId> {
    public User findByUsername(String username);
}

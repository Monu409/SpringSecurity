package com.example.security.SpringSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class User {
    @Id
    private ObjectId objectId;
    private String username;
    private String password;
    private String role;
}

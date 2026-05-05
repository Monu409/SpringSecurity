package com.example.security.SpringSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "social_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialModel {
    @Id
    private String id;
    private String email;
    private String name;
    private String mobile;
    private String loginType;
    private String loginId;
    private String profilePicture;
    private String fcmToken;

}

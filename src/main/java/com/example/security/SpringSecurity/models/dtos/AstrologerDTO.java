package com.example.security.SpringSecurity.models.dtos;

import lombok.Data;

@Data
public class AstrologerDTO {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phone;
    private String experience;
    private String expertType;
    private String about;
    private String chatRate;
    private String callRate;
    private String firebaseToken;
}

package com.example.security.SpringSecurity.models.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String loginId;
    private String name;
    private String mobile;
    private String profilePicture;
    private String fcmToken;
}

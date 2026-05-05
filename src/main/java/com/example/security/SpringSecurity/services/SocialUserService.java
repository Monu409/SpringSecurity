package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.SocialModel;
import com.example.security.SpringSecurity.models.dtos.UserDTO;
import com.example.security.SpringSecurity.repos.SocialUserRepo;
import com.example.security.SpringSecurity.utils.CommonResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SocialUserService {
    @Autowired
    private SocialUserRepo socialUserRepo;

    public ResponseEntity<?> addSocialUser(SocialModel socialModel){
        socialUserRepo.save(socialModel);
        return new ResponseEntity<>(
                new CommonResDTO<>(true, "User Added Successfully", socialModel),
                HttpStatus.OK
        );
    }

    public ResponseEntity<?> getUserById(SocialModel socialModel){
        Optional<SocialModel> user = socialUserRepo.findByLoginId(socialModel.getLoginId());
        if(user.isPresent()){
            SocialModel existing = user.get();
            existing.setFcmToken(socialModel.getFcmToken());
            socialUserRepo.save(existing);
            return new ResponseEntity<>(
                    new CommonResDTO<>(true, "User Found", existing),
                    HttpStatus.OK
            );
        }
        return addSocialUser(socialModel);
    }

    public ResponseEntity<?> getUserProfile(String id) {
        try {
            Optional<SocialModel> optional = socialUserRepo.findById(id);
            if (optional.isPresent()) {
                return new ResponseEntity<>(
                        new CommonResDTO<>(true, "Profile fetched successfully", optional.get()),
                        HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "User not found", null),
                    HttpStatus.NOT_FOUND
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Invalid ID format", null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public ResponseEntity<?> updateUserProfile(String id, UserDTO dto) {
        try {
            Optional<SocialModel> optional = socialUserRepo.findById(id);
            if (optional.isEmpty()) {
                return new ResponseEntity<>(
                        new CommonResDTO<>(false, "User not found", null),
                        HttpStatus.NOT_FOUND
                );
            }
            SocialModel user = optional.get();
            if (dto.getName() != null)           user.setName(dto.getName());
            if (dto.getMobile() != null)         user.setMobile(dto.getMobile());
            if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
            if (dto.getFcmToken() != null)       user.setFcmToken(dto.getFcmToken());
            socialUserRepo.save(user);
            return new ResponseEntity<>(
                    new CommonResDTO<>(true, "Profile updated successfully", user),
                    HttpStatus.OK
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Invalid ID format", null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public List<SocialModel> findAllUsers() {
        return socialUserRepo.findAll();
    }
}

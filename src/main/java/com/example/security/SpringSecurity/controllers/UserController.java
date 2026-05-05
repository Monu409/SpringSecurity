package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.models.SocialModel;
import com.example.security.SpringSecurity.models.dtos.UserDTO;
import com.example.security.SpringSecurity.services.SocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SocialUserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody SocialModel socialModel){
        return userService.addSocialUser(socialModel);
    }

    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser(@RequestBody SocialModel socialModel){
        return userService.getUserById(socialModel);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id){
        return userService.getUserProfile(id);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, @RequestBody UserDTO userDTO){
        return userService.updateUserProfile(id, userDTO);
    }

    @GetMapping("/test1")
    public List<SocialModel> test(){
        return userService.findAllUsers();
    }
}

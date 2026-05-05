package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.models.User;
import com.example.security.SpringSecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/get-admin")
    public List<User> getAllAdmins(){
        return userService.getUserAndAdmin();
    }
}

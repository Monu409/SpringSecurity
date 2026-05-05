package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.User;
import com.example.security.SpringSecurity.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return true;
    }

    public List<User> getAllUsers(){
        List<User> allUsers = userRepo.findAll();
        return allUsers.stream().filter(user -> user.getRole().equals("User")).toList();
    }

    public List<User> getUserAndAdmin(){
        return userRepo.findAll();
    }
}

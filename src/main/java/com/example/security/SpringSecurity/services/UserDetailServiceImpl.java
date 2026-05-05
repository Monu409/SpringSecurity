package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.User;
import com.example.security.SpringSecurity.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byUsername = userRepo.findByUsername(username);
        if(byUsername==null){
            throw new RuntimeException("User not found with the given name");
        }
        else{
            return org.springframework.security.core.userdetails.User.builder()
                    .username(byUsername.getUsername())
                    .password(byUsername.getPassword())
                    .roles(byUsername.getRole())
                    .build();
        }
    }
}

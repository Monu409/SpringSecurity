package com.example.security.SpringSecurity.config;

import com.example.security.SpringSecurity.services.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailServiceImpl userDetailService;

    public SecurityConfig(UserDetailServiceImpl userDetailService) {
        this.userDetailService = userDetailService;
    }
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         http
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/", "/public/**","/astrologers/**","/user/**","/product-manager/**","/review/**").permitAll()
// //                        .requestMatchers("/user/**").hasAnyRole("User","Admin")
//                         .requestMatchers("/admin/**").hasRole("Admin")
//                         .anyRequest().authenticated()
//                 )
//                 .userDetailsService(userDetailService)
//                 .httpBasic(Customizer.withDefaults())
//                 .csrf(AbstractHttpConfigurer::disable);

//         return http.build();
//     }
    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        );
    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

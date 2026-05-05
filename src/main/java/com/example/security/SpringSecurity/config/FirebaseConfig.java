package com.example.security.SpringSecurity.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Configuration
public class FirebaseConfig {

    private static final Logger log = Logger.getLogger(FirebaseConfig.class.getName());

    @Bean
    public FirebaseApp firebaseApp() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try {
            InputStream serviceAccount;
            String credentialsJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
            if (credentialsJson != null && !credentialsJson.isBlank()) {
                serviceAccount = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
            } else {
                ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                if (!resource.exists()) {
                    log.warning("Firebase credentials not found — set FIREBASE_SERVICE_ACCOUNT_JSON env var. Firebase features will be unavailable.");
                    return null;
                }
                serviceAccount = resource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.warning("Failed to initialize Firebase: " + e.getMessage() + ". Firebase features will be unavailable.");
            return null;
        }
    }
}

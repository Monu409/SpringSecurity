package com.example.security.SpringSecurity.services;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseNotificationService {

    public boolean sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .setSound("default")
                                    .build())
                            .build());
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            System.out.println("FCM notification sent successfully. Message ID: " + response);
            return true;
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send FCM notification: " + e.getMessage());
            return false;
        }
    }
}

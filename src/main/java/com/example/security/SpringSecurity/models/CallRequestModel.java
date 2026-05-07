package com.example.security.SpringSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "call_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallRequestModel {
    @Id
    private String id;
    private String userId;
    private String astrologerId;
    private String status;
    private LocalDateTime requestTime;
    private String callType;
    private String chatRoomId;
}

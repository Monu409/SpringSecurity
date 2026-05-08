package com.example.security.SpringSecurity.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallStatusMessage {
    private String callRequestId;
    private String userId;
    private String astrologerId;
    private String status;       // PENDING | ACCEPTED | REJECTED | NOT_ANSWERED | ENDED
    private String callType;
    private String chatRoomId;
    private String message;
}
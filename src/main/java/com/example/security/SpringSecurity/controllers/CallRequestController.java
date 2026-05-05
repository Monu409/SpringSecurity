package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.services.CallRequestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class CallRequestController {

    @Autowired
    private CallRequestService callRequestService;

    @PostMapping("/call-request")
    public ResponseEntity<?> initiateCallRequest(@RequestBody CallRequestBody body) {
        return callRequestService.initiateCallRequest(
                body.getUserId(),
                body.getAstrologerId(),
                body.getCallType()
        );
    }

    @Data
    public static class CallRequestBody {
        private String userId;
        private String astrologerId;
        private String callType;
    }
}

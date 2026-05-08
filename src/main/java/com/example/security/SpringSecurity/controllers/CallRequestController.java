package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.services.CallRequestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class CallRequestController {

    @Autowired
    private CallRequestService callRequestService;

    // User initiates a call
    @PostMapping("/call-request")
    public ResponseEntity<?> initiateCallRequest(@RequestBody CallRequestBody body) {
        return callRequestService.initiateCallRequest(
                body.getUserId(),
                body.getAstrologerId(),
                body.getCallType()
        );
    }

    // Astrologer accepts
    @PostMapping("/call-request/{callRequestId}/accept")
    public ResponseEntity<?> acceptCall(
            @PathVariable String callRequestId,
            @RequestParam String astrologerId) {
        return callRequestService.acceptCallRequest(callRequestId, astrologerId);
    }

    // Astrologer rejects
    @PostMapping("/call-request/{callRequestId}/reject")
    public ResponseEntity<?> rejectCall(
            @PathVariable String callRequestId,
            @RequestParam String astrologerId) {
        return callRequestService.rejectCallRequest(callRequestId, astrologerId);
    }

    // System/client marks as not answered (timeout)
    @PostMapping("/call-request/{callRequestId}/not-answered")
    public ResponseEntity<?> notAnswered(@PathVariable String callRequestId) {
        return callRequestService.markNotAnswered(callRequestId);
    }

    // Either side ends the active call
    @PostMapping("/call-request/{callRequestId}/end")
    public ResponseEntity<?> endCall(@PathVariable String callRequestId) {
        return callRequestService.endCall(callRequestId);
    }

    @Data
    public static class CallRequestBody {
        private String userId;
        private String astrologerId;
        private String callType;
    }
}
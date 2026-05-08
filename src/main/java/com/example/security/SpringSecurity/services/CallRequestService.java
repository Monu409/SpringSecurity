package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.AstrologerModel;
import com.example.security.SpringSecurity.models.CallRequestModel;
import com.example.security.SpringSecurity.models.SocialModel;
import com.example.security.SpringSecurity.models.dtos.CallStatusMessage;
import com.example.security.SpringSecurity.repos.AstrologerRepo;
import com.example.security.SpringSecurity.repos.CallRequestRepo;
import com.example.security.SpringSecurity.repos.SocialUserRepo;
import com.example.security.SpringSecurity.utils.CommonResDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CallRequestService {

    @Autowired private SocialUserRepo socialUserRepo;
    @Autowired private AstrologerRepo astrologerRepo;
    @Autowired private CallRequestRepo callRequestRepo;
    @Autowired private FirebaseNotificationService firebaseNotificationService;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    // ── User initiates call ──────────────────────────────────────────────────

    public ResponseEntity<?> initiateCallRequest(String userId, String astrologerId, String callType) {
        Optional<SocialModel> userOpt = socialUserRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return bad("User not found");
        }

        Optional<AstrologerModel> astroOpt;
        try {
            astroOpt = astrologerRepo.findById(new ObjectId(astrologerId));
        } catch (IllegalArgumentException e) {
            return bad("Invalid astrologerId format");
        }
        if (astroOpt.isEmpty()) {
            return bad("Astrologer not found");
        }

        SocialModel user = userOpt.get();
        AstrologerModel astrologer = astroOpt.get();

        String roomId = "ROOM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        CallRequestModel callRequest = new CallRequestModel();
        callRequest.setUserId(userId);
        callRequest.setAstrologerId(astrologerId);
        callRequest.setStatus("PENDING");
        callRequest.setRequestTime(LocalDateTime.now());
        callRequest.setCallType(callType);
        callRequest.setChatRoomId(roomId);
        CallRequestModel saved = callRequestRepo.save(callRequest);

        // Push FCM to astrologer
        String firebaseToken = astrologer.getFirebaseToken();
        if (firebaseToken != null && !firebaseToken.isEmpty()) {
            String title = "Incoming " + callType + " Request";
            String body = (user.getName() != null ? user.getName() : "A user") + " wants a " + callType + " with you";
            Map<String, String> data = new HashMap<>();
            data.put("callRequestId", saved.getId());
            data.put("userId", userId);
            data.put("callType", callType);
            data.put("userName", user.getName() != null ? user.getName() : "");
            data.put("roomId", roomId);
            firebaseNotificationService.sendNotification(firebaseToken, title, body, data);
        }

        // WebSocket → notify astrologer of incoming request
        pushToAstrologer(saved, "New " + callType + " request from " + (user.getName() != null ? user.getName() : "user"));

        return ok("Call request sent successfully", saved);
    }

    // ── Astrologer responds ──────────────────────────────────────────────────

    public ResponseEntity<?> acceptCallRequest(String callRequestId, String astrologerId) {
        Optional<CallRequestModel> reqOpt = callRequestRepo.findById(callRequestId);
        if (reqOpt.isEmpty()) return bad("Call request not found");

        CallRequestModel callRequest = reqOpt.get();
        if (!callRequest.getAstrologerId().equals(astrologerId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CommonResDTO<>(false, "Unauthorized", null));

        if (!"PENDING".equals(callRequest.getStatus()))
            return bad("Call request is no longer pending");

        callRequest.setStatus("ACCEPTED");
        callRequestRepo.save(callRequest);

        // FCM → notify user
        socialUserRepo.findById(callRequest.getUserId()).ifPresent(user -> {
            String fcmToken = user.getFcmToken();
            if (fcmToken != null && !fcmToken.isEmpty()) {
                Map<String, String> data = new HashMap<>();
                data.put("type", "call_accepted");
                data.put("chatRoomId", callRequest.getChatRoomId());
                data.put("callRequestId", callRequestId);
                firebaseNotificationService.sendNotification(fcmToken, "Call Accepted", "Astrologer accepted your request", data);
            }
        });

        // WebSocket → notify user in real time
        pushToUser(callRequest, "Astrologer accepted your call request");

        return ok("Call request accepted", callRequest);
    }

    public ResponseEntity<?> rejectCallRequest(String callRequestId, String astrologerId) {
        Optional<CallRequestModel> reqOpt = callRequestRepo.findById(callRequestId);
        if (reqOpt.isEmpty()) return bad("Call request not found");

        CallRequestModel callRequest = reqOpt.get();
        if (!callRequest.getAstrologerId().equals(astrologerId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CommonResDTO<>(false, "Unauthorized", null));

        if (!"PENDING".equals(callRequest.getStatus()))
            return bad("Call request is no longer pending");

        callRequest.setStatus("REJECTED");
        callRequestRepo.save(callRequest);

        // FCM → notify user
        socialUserRepo.findById(callRequest.getUserId()).ifPresent(user -> {
            String fcmToken = user.getFcmToken();
            if (fcmToken != null && !fcmToken.isEmpty()) {
                Map<String, String> data = new HashMap<>();
                data.put("type", "call_rejected");
                data.put("callRequestId", callRequestId);
                firebaseNotificationService.sendNotification(fcmToken, "Call Rejected", "Astrologer is unavailable", data);
            }
        });

        // WebSocket → notify user in real time
        pushToUser(callRequest, "Astrologer rejected your call request");

        return ok("Call request rejected", callRequest);
    }

    public ResponseEntity<?> markNotAnswered(String callRequestId) {
        Optional<CallRequestModel> reqOpt = callRequestRepo.findById(callRequestId);
        if (reqOpt.isEmpty()) return bad("Call request not found");

        CallRequestModel callRequest = reqOpt.get();
        if (!"PENDING".equals(callRequest.getStatus()))
            return bad("Call request is no longer pending");

        callRequest.setStatus("NOT_ANSWERED");
        callRequestRepo.save(callRequest);

        // WebSocket → notify both sides
        pushToUser(callRequest, "Astrologer did not answer");
        pushToAstrologer(callRequest, "Call request expired — not answered");

        return ok("Marked as not answered", callRequest);
    }

    public ResponseEntity<?> endCall(String callRequestId) {
        Optional<CallRequestModel> reqOpt = callRequestRepo.findById(callRequestId);
        if (reqOpt.isEmpty()) return bad("Call request not found");

        CallRequestModel callRequest = reqOpt.get();
        if (!"ACCEPTED".equals(callRequest.getStatus()))
            return bad("Call is not active");

        callRequest.setStatus("ENDED");
        callRequestRepo.save(callRequest);

        // WebSocket → notify both sides
        pushToUser(callRequest, "Call has ended");
        pushToAstrologer(callRequest, "Call has ended");

        return ok("Call ended", callRequest);
    }

    // ── WebSocket helpers ────────────────────────────────────────────────────

    // Sends status update to the user who made the call request
    // Client subscribes to: /topic/call-status/{userId}
    private void pushToUser(CallRequestModel req, String message) {
        CallStatusMessage msg = toStatusMessage(req, message);
        messagingTemplate.convertAndSend("/topic/call-status/" + req.getUserId(), msg);
    }

    // Sends incoming/update to the astrologer
    // Client subscribes to: /topic/call-status/astro/{astrologerId}
    private void pushToAstrologer(CallRequestModel req, String message) {
        CallStatusMessage msg = toStatusMessage(req, message);
        messagingTemplate.convertAndSend("/topic/call-status/astro/" + req.getAstrologerId(), msg);
    }

    private CallStatusMessage toStatusMessage(CallRequestModel req, String message) {
        return new CallStatusMessage(
                req.getId(),
                req.getUserId(),
                req.getAstrologerId(),
                req.getStatus(),
                req.getCallType(),
                req.getChatRoomId(),
                message
        );
    }

    // ── Response helpers ─────────────────────────────────────────────────────

    private ResponseEntity<?> ok(String message, Object data) {
        return new ResponseEntity<>(new CommonResDTO<>(true, message, data), HttpStatus.OK);
    }

    private ResponseEntity<?> bad(String message) {
        return new ResponseEntity<>(new CommonResDTO<>(false, message, null), HttpStatus.BAD_REQUEST);
    }
}
package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.AstrologerModel;
import com.example.security.SpringSecurity.models.CallRequestModel;
import com.example.security.SpringSecurity.models.SocialModel;
import com.example.security.SpringSecurity.repos.AstrologerRepo;
import com.example.security.SpringSecurity.repos.CallRequestRepo;
import com.example.security.SpringSecurity.repos.SocialUserRepo;
import com.example.security.SpringSecurity.utils.CommonResDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CallRequestService {

    @Autowired
    private SocialUserRepo socialUserRepo;

    @Autowired
    private AstrologerRepo astrologerRepo;

    @Autowired
    private CallRequestRepo callRequestRepo;

    @Autowired
    private FirebaseNotificationService firebaseNotificationService;

    public ResponseEntity<?> initiateCallRequest(String userId, String astrologerId, String callType) {

        Optional<SocialModel> userOpt = socialUserRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "User not found", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<AstrologerModel> astroOpt;
        try {
            astroOpt = astrologerRepo.findById(new ObjectId(astrologerId));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Invalid astrologerId format", null),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (astroOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Astrologer not found", null),
                    HttpStatus.BAD_REQUEST
            );
        }

        SocialModel user = userOpt.get();
        AstrologerModel astrologer = astroOpt.get();

        CallRequestModel callRequest = new CallRequestModel();
        callRequest.setUserId(userId);
        callRequest.setAstrologerId(astrologerId);
        callRequest.setStatus("PENDING");
        callRequest.setRequestTime(LocalDateTime.now());
        callRequest.setCallType(callType);
        CallRequestModel savedRequest = callRequestRepo.save(callRequest);

        String firebaseToken = astrologer.getFirebaseToken();
        if (firebaseToken != null && !firebaseToken.isEmpty()) {
            String title = "Incoming " + callType + " Request";
            String body = (user.getName() != null ? user.getName() : "A user")
                          + " wants a " + callType + " with you";
            Map<String, String> data = new HashMap<>();
            data.put("callRequestId", savedRequest.getId());
            data.put("userId", userId);
            data.put("callType", callType);
            data.put("userName", user.getName() != null ? user.getName() : "");
            firebaseNotificationService.sendNotification(firebaseToken, title, body, data);
        }

        return new ResponseEntity<>(
                new CommonResDTO<>(true, "Call request sent successfully", savedRequest),
                HttpStatus.OK
        );
    }
}

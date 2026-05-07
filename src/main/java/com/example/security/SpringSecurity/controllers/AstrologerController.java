package com.example.security.SpringSecurity.controllers;

import com.example.security.SpringSecurity.models.AstrologerModel;
import com.example.security.SpringSecurity.models.dtos.AstrologerDTO;
import com.example.security.SpringSecurity.services.AstrologerService;
import com.example.security.SpringSecurity.services.CallRequestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/astrologers")
public class AstrologerController {
    @Autowired
    private AstrologerService astrologerService;

    @Autowired
    private CallRequestService callRequestService;

    @PostMapping("/add")
    public ResponseEntity<?> addAstrologer(@RequestBody AstrologerModel astrologerModel){
        return astrologerService.addAstrologer(astrologerModel);
    }

    @PostMapping("/find-all")
    public ResponseEntity<?> getAllAstrologer(@RequestBody AstrologerDTO astrologerDTO){
        return astrologerService.getAllAstrologers(astrologerDTO);
    }

    @PostMapping("/login-astro")
    public ResponseEntity<?> loginAstrologer(@RequestBody AstrologerDTO astrologerDTO){
        return astrologerService.loginAstrologer(astrologerDTO);
    }
//mongodb+srv://monu-user:Database11@cluster0.mauhbcv.mongodb.net/?appName=Cluster0
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getAstrologerProfile(@PathVariable String id){
        return astrologerService.getAstrologerProfile(id);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateAstrologerProfile(@PathVariable String id, @RequestBody AstrologerDTO astrologerDTO){
        return astrologerService.updateAstrologerProfile(id, astrologerDTO);
    }

    @PostMapping("/accept-call")
    public ResponseEntity<?> acceptCallRequest(@RequestBody AcceptCallBody body) {
        return callRequestService.acceptCallRequest(body.getCallRequestId(), body.getAstrologerId());
    }

    @Data
    public static class AcceptCallBody {
        private String callRequestId;
        private String astrologerId;
    }

}

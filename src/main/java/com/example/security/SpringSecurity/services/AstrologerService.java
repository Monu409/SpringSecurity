package com.example.security.SpringSecurity.services;

import com.example.security.SpringSecurity.models.AstrologerModel;
import com.example.security.SpringSecurity.models.dtos.AstrologerDTO;
import com.example.security.SpringSecurity.repos.AstrologerRepo;
import com.example.security.SpringSecurity.utils.CommonResDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AstrologerService {
    @Autowired
    private AstrologerRepo astrologerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> addAstrologer(AstrologerModel astrologerModel){
        Optional<AstrologerModel> existing = astrologerRepo.findByEmail(astrologerModel.getEmail());
        if (existing.isPresent()) {
            CommonResDTO<?> res = new CommonResDTO<>(false, "Astrologer already registered", null);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        astrologerModel.setPassword(passwordEncoder.encode(astrologerModel.getPassword()));
        astrologerRepo.save(astrologerModel);
        CommonResDTO<?> commonResDTO = new CommonResDTO<>(true,"Astrologer added successfully",astrologerModel);
        return new ResponseEntity<>(commonResDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllAstrologers(AstrologerDTO astrologerDTO){
        List<AstrologerModel> astrologerModel;
        if((astrologerDTO.getFirstName()==null || astrologerDTO.getFirstName().isEmpty()) &&
                (astrologerDTO.getLastName()==null || astrologerDTO.getLastName().isEmpty()) &&
                (astrologerDTO.getEmail()==null || astrologerDTO.getEmail().isEmpty()) &&
                (astrologerDTO.getPhone()==null || astrologerDTO.getPhone().isEmpty())
        ){
            astrologerModel = astrologerRepo.findAll();
        }
        else  {
            astrologerModel = astrologerRepo.findAll()
                    .stream()
                    .filter(astro -> astro.getFirstName()
                            .toLowerCase()
                            .contains(astrologerDTO.getFirstName().toLowerCase()))
                    .toList();
        }
        CommonResDTO<?> commonResDTO = new CommonResDTO<>(
             true,
             "Fatch all astro",
                astrologerModel
        );
        return new ResponseEntity<>(commonResDTO,HttpStatus.OK);
    }

    public ResponseEntity<?> loginAstrologer(AstrologerDTO astrologerDTO){
        Optional<AstrologerModel> optionalAstro = astrologerRepo.findByEmail(astrologerDTO.getEmail());
        if(optionalAstro.isPresent()){
          AstrologerModel astrologerModel = optionalAstro.get();
          if(passwordEncoder.matches((astrologerDTO.getPassword()), astrologerModel.getPassword())){
              astrologerModel.setFirebaseToken(astrologerDTO.getFirebaseToken());
              astrologerRepo.save(astrologerModel);
              CommonResDTO<AstrologerModel> res = new CommonResDTO<>(
                      true,
                      "Astrologer Found",
                      astrologerModel
              );
              return new ResponseEntity<>(res,HttpStatus.OK);
          }
          else{
              CommonResDTO<?> res = new CommonResDTO<>(
                      false,
                      "Email or Password is incorrect",
                      null
              );
              return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
          }
        }
        CommonResDTO<?> res = new CommonResDTO<>(
                false,
                "User not found",
                null
        );
        return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getAstrologerProfile(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<AstrologerModel> optional = astrologerRepo.findById(objectId);
            if (optional.isPresent()) {
                return new ResponseEntity<>(
                    new CommonResDTO<>(true, "Profile fetched successfully", optional.get()),
                    HttpStatus.OK
                );
            }
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Astrologer not found", null),
                HttpStatus.NOT_FOUND
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Invalid ID format", null),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    public ResponseEntity<?> updateAstrologerProfile(String id, AstrologerDTO dto) {
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<AstrologerModel> optional = astrologerRepo.findById(objectId);
            if (optional.isEmpty()) {
                return new ResponseEntity<>(
                    new CommonResDTO<>(false, "Astrologer not found", null),
                    HttpStatus.NOT_FOUND
                );
            }
            AstrologerModel astrologer = optional.get();
            if (dto.getFirstName() != null)     astrologer.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null)      astrologer.setLastName(dto.getLastName());
            if (dto.getPhone() != null)         astrologer.setPhone(dto.getPhone());
            if (dto.getExperience() != null)    astrologer.setExperience(dto.getExperience());
            if (dto.getExpertType() != null)    astrologer.setExpertType(dto.getExpertType());
            if (dto.getAbout() != null)         astrologer.setAbout(dto.getAbout());
            if (dto.getChatRate() != null)      astrologer.setChatRate(dto.getChatRate());
            if (dto.getCallRate() != null)      astrologer.setCallRate(dto.getCallRate());
            if (dto.getFirebaseToken() != null) astrologer.setFirebaseToken(dto.getFirebaseToken());
            astrologerRepo.save(astrologer);
            return new ResponseEntity<>(
                new CommonResDTO<>(true, "Profile updated successfully", astrologer),
                HttpStatus.OK
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                new CommonResDTO<>(false, "Invalid ID format", null),
                HttpStatus.BAD_REQUEST
            );
        }
    }
}

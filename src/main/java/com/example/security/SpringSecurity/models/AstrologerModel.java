package com.example.security.SpringSecurity.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AstrologerModel {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId objectId;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String phone;
    private String experience;
    private String expertType;
    private String about;
    private String chatRate;
    private String callRate;
    private String firebaseToken;
    private Double averageRating;
    private List<ReviewModel> topReviews;
}

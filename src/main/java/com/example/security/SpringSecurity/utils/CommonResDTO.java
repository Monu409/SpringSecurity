package com.example.security.SpringSecurity.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResDTO<T> {
    private boolean status;
    private String message;
    private T data;
}

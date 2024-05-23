package com.example.sbg.api.models;

import lombok.Data;

@Data
public class Error {
    private int httpCode;
    private int errorCode;
    private String message;

    public Error(int httpCode, int errorCode, String message) {
        this.httpCode = httpCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
package com.example.LicenseAPI.exception;

public class InvalidBINException extends RuntimeException {
    public InvalidBINException(String message) {
        super(message);
    }
}

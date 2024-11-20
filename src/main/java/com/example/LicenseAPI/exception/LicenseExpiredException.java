package com.example.LicenseAPI.exception;

public class LicenseExpiredException extends RuntimeException {
    public LicenseExpiredException(String message) {
            super(message);
    }
}

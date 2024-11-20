package com.example.LicenseAPI.exception;

public class LicenseAlreadyUsedException extends RuntimeException {
    public LicenseAlreadyUsedException(String message) {
        super(message);
    }
}

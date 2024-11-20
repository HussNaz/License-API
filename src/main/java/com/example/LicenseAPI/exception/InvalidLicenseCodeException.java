package com.example.LicenseAPI.exception;

public class InvalidLicenseCodeException extends RuntimeException {
    public InvalidLicenseCodeException(String message) {
        super(message);
    }
}

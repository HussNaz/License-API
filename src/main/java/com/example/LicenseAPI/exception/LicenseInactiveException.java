package com.example.LicenseAPI.exception;

public class LicenseInactiveException extends RuntimeException {
    public LicenseInactiveException(String message) {
        super(message);
    }
}

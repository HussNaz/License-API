package com.example.LicenseAPI.responseDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidationResponse {
    private boolean isValid;
    private String message;
}

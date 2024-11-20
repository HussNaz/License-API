package com.example.LicenseAPI.controller;

import com.example.LicenseAPI.model.License;
import com.example.LicenseAPI.service.LicenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LicenseController {
    private final LicenseService licenseService;

    @PostMapping("/create")
    public ResponseEntity<License> createLicense(@Valid @RequestBody License license) {
        License createdLicense = licenseService.createLicense(license);
        return ResponseEntity.status(201).body(createdLicense);
    }

    @PutMapping("/activate/{licenseCode}")
    public ResponseEntity<Map<String, String>> activateLicense(@PathVariable String licenseCode) {
        License activatedLicense = licenseService.activateLicense(licenseCode);
        Map<String, String> response = new HashMap<>();
        response.put("message", "License with code " + activatedLicense.getLicenseCode() + " has been activated.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/deactivate/{licenseCode}")
    public ResponseEntity<Map<String, String>> deactivateLicense(@PathVariable String licenseCode) {
        License deactivatedLicense = licenseService.deactivateLicense(licenseCode);
        Map<String, String> response = new HashMap<>();
        response.put("message", "License with code " + deactivatedLicense.getLicenseCode() + " has been deactivated.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("getLicenseById/{id}")
    public ResponseEntity<License> getLicenseById(@PathVariable Long id) {
        License license = licenseService.getLicenseById(id);
        return ResponseEntity.ok(license);
    }

    @GetMapping("/getByLicenseCode/{licenseCode}")
    public ResponseEntity<License> getLicenseByCode(@PathVariable String licenseCode) {
        License license = licenseService.getLicenseByCode(licenseCode);
        return ResponseEntity.ok(license);
    }

    @GetMapping("/getAllLicenses")
    public ResponseEntity<List<License>> getAllLicenses() {
        List<License> licenses = licenseService.getAllLicenses();
        return ResponseEntity.ok(licenses);
    }

    @GetMapping("/validate/{licenseCode}")
    public ResponseEntity<Map<String, String>> validateLicense(
            @PathVariable String licenseCode,
            @RequestParam String binNumber,
            @RequestParam String nid) {

        boolean isValid= licenseService.validateLicense(licenseCode, binNumber, nid);
        Map<String, String> response = new HashMap<>();
        response.put("isValid", String.valueOf(isValid));
        response.put("message", "License with code " + licenseCode + " is valid.");
        return ResponseEntity.ok(response);
    }
}


package com.example.LicenseAPI.service;

import com.example.LicenseAPI.exception.*;
import com.example.LicenseAPI.model.License;
import com.example.LicenseAPI.repository.LicenseRepository;
import com.example.LicenseAPI.responseDTO.ValidationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LicenseService {
    @Value("${IVAS_URL}")
    private String ivas_url;

    private final RestTemplate restTemplate;

    private final LicenseRepository licenseRepository;

    public List<License> getAllLicenses() {
        return licenseRepository.findAll();
    }

    @Transactional
    public License createLicense(License license) {
        if (!validateBinAndNid(license.getBinNumber(), license.getNid())) {
            throw new BinOrNidNumberMismatchException("Invalid binNumber or nid.");
        }
        return licenseRepository.save(license);
    }

    @Transactional
    public License activateLicense(String licenseCode) {
        License license = licenseRepository.findByLicenseCode(licenseCode)
                .orElseThrow(() -> new LicenseNotFoundException("License with code " + licenseCode + " not found."));
        if (license.getIsActive()) {
            throw new RuntimeException("License is already active.");
        }
        license.setIsActive(true);
        return licenseRepository.save(license);
    }

    @Transactional
    public License deactivateLicense(String licenseCode) {
        License license = licenseRepository.findByLicenseCode(licenseCode)
                .orElseThrow(() -> new LicenseNotFoundException("License with code " + licenseCode + " not found."));
        if (!license.getIsActive()) {
            throw new RuntimeException("License is already inactive.");
        }
        license.setIsActive(false);
        return licenseRepository.save(license);
    }

    public License getLicenseById(Long id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new LicenseNotFoundException("License with ID " + id + " not found."));
    }

    public License getLicenseByCode(String licenseCode) {
        if (!isValidLicenseCode(licenseCode)) {
            throw new InvalidLicenseCodeException("Invalid license code.");
        }
        return licenseRepository.findByLicenseCode(licenseCode)
                .orElseThrow(() -> new LicenseNotFoundException("License with code " + licenseCode + " not found."));
    }



    public boolean validateLicense(String licenseCode, String binNumber, String nid) {

        if (!isValidLicenseCode(licenseCode)) {
            throw new InvalidLicenseCodeException("Invalid license code.");
        }

        if (!validateBinAndNid(binNumber, nid)) {
            throw new BinOrNidNumberMismatchException("Invalid binNumber or nid.");
        }

        License license = licenseRepository.findByLicenseCode(licenseCode)
                .orElseThrow(() -> new LicenseNotFoundException("License with code " + licenseCode + " not found."));

        if (!license.getBinNumber().equals(binNumber)) {
            throw new InvalidBINException("Invalid binNumber for the provided licenseCode.");
        }

        if (!license.getNid().equals(nid)) {
            throw new InvalidNIDException("Invalid nid for the provided licenseCode.");
        }

        if (!license.getIsActive()) {
            throw new LicenseInactiveException("License with code " + licenseCode + " is not active.");
        }
        if (license.getIsOneTimeUse()) {
            throw new LicenseAlreadyUsedException("License with code " + licenseCode + " is already one time used.");
        }
        if (license.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new LicenseExpiredException("License with code " + licenseCode + " has expired.");
        }

        return true;
    }

    private boolean validateBinAndNid(String binNumber, String nid) {
        try {
            String url = String.format("%s?binNumber=%s&nid=%s", ivas_url, binNumber, nid);

             return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error from external service: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during validation: " + e.getMessage());
        }
    }

    private boolean isValidLicenseCode(String licenseCode) {
        return licenseCode.matches("\\d{4}");
    }
}

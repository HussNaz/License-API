package com.example.LicenseAPI.repository;

import com.example.LicenseAPI.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByLicenseCode(String licenseCode);
}

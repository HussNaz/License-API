package com.example.LicenseAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "License Name is required")
    @Size(min = 3, max = 50, message = "License Name must be between 3 and 50 characters")
    @Column(nullable = false)
    private String licenseName;

    @NotBlank(message = "Bin Number is required")
    @Pattern(regexp = "\\d{13}", message = "Bin Number must be a 13-digit string")
    @Column(nullable = false)
    private String binNumber;

    @NotBlank(message = "National ID (NID) is required")
    @Pattern(regexp = "\\d{10}|\\d{13}|\\d{18}", message = "NID must be 10, 13, or 18 digits")
    @Column(nullable = false)
    private String nid;

    @Column(nullable = false, unique = true, updatable = false)
    @Schema(hidden = true)
    private String licenseCode;

    @Column(nullable = false)
    @Schema(hidden = true)
    private Boolean isActive = false;

    @Column(nullable = false)
    @Schema(hidden = true)
    private Boolean isOneTimeUse = false;

    @Column(nullable = false, updatable = false)
    @Schema(hidden = true)
    private LocalDateTime issuedDate= LocalDateTime.now();

    @NotNull(message = "Expiration Date is required")
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @PrePersist
    private void generateLicenseCode() {
        this.licenseCode = String.format("%04d", (int) (Math.random() * 10000));
    }
}

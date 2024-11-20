package com.example.LicenseAPI;

import com.example.LicenseAPI.model.License;
import com.example.LicenseAPI.repository.LicenseRepository;
import com.example.LicenseAPI.service.LicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LicenseServiceTest {
    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LicenseService licenseService;

    private  License testLicense;


    @BeforeEach
    void setUP() {
        testLicense = new License(
                1L,
                "Nazmul",
                "0318708469781",
                "1234567890",
                "8712",
                false,
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
    }

    @Test
    void testCreateLicense() {
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        License createdLicense = licenseService.createLicense(testLicense);

        assertNotNull(createdLicense);
        assertEquals(testLicense.getLicenseCode(), createdLicense.getLicenseCode());
        verify(licenseRepository,times(1)).save(testLicense);

    }
}

package com.example.LicenseAPI;
import com.example.LicenseAPI.exception.BinOrNidNumberMismatchException;
import com.example.LicenseAPI.exception.InvalidLicenseCodeException;
import com.example.LicenseAPI.exception.LicenseNotFoundException;
import com.example.LicenseAPI.model.License;
import com.example.LicenseAPI.repository.LicenseRepository;
import com.example.LicenseAPI.service.LicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LicenseService licenseService;

    @Value("${IVAS_URL}")
    private String ivas_url;

    private License testLicense;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testLicense = new License();
        testLicense.setId(1L);
        testLicense.setLicenseName("Test License");
        testLicense.setBinNumber("1234567890123");
        testLicense.setNid("1234567890");
        testLicense.setLicenseCode("8712");
        testLicense.setIsActive(false);
        testLicense.setIsOneTimeUse(false);
        testLicense.setIssuedDate(LocalDateTime.now());
        testLicense.setExpirationDate(LocalDateTime.now().plusDays(30));
    }

    @Test
    public void testGetAllLicenses() {
        when(licenseRepository.findAll()).thenReturn(List.of(testLicense));

        assertEquals(1, licenseService.getAllLicenses().size());
        verify(licenseRepository, times(1)).findAll();
    }

    @Test
    public void testCreateLicense_Success() {
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(licenseRepository.save(testLicense)).thenReturn(testLicense);

        License result = licenseService.createLicense(testLicense);
        assertNotNull(result);
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    public void testCreateLicense_InvalidBinOrNid() {
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(false);

        assertThrows(BinOrNidNumberMismatchException.class, () -> licenseService.createLicense(testLicense));
        verify(licenseRepository, never()).save(any(License.class));
    }

    @Test
    public void testActivateLicense_Success() {
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.of(testLicense));

        License result = licenseService.activateLicense(testLicense.getLicenseCode());

        assertTrue(result.getIsActive());
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    public void testActivateLicense_NotFound() {
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.activateLicense(testLicense.getLicenseCode()));
    }

    @Test
    public void testDeactivateLicense_Success() {
        testLicense.setIsActive(true);
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.of(testLicense));

        License result = licenseService.deactivateLicense(testLicense.getLicenseCode());

        assertFalse(result.getIsActive());
        verify(licenseRepository, times(1)).save(testLicense);
    }

    @Test
    public void testDeactivateLicense_NotFound() {
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.deactivateLicense(testLicense.getLicenseCode()));
    }

    @Test
    public void testGetLicenseById_Success() {
        when(licenseRepository.findById(testLicense.getId())).thenReturn(Optional.of(testLicense));

        License result = licenseService.getLicenseById(testLicense.getId());

        assertNotNull(result);
        assertEquals(testLicense.getId(), result.getId());
    }

    @Test
    public void testGetLicenseById_NotFound() {
        when(licenseRepository.findById(testLicense.getId())).thenReturn(Optional.empty());

        assertThrows(LicenseNotFoundException.class, () -> licenseService.getLicenseById(testLicense.getId()));
    }

    @Test
    public void testGetLicenseByCode_Success() {
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.of(testLicense));

        License result = licenseService.getLicenseByCode(testLicense.getLicenseCode());

        assertNotNull(result);
        assertEquals(testLicense.getLicenseCode(), result.getLicenseCode());
    }

    @Test
    public void testGetLicenseByCode_InvalidCode() {
        assertThrows(InvalidLicenseCodeException.class, () -> licenseService.getLicenseByCode("123"));
    }

    @Test
    public void testValidateLicenseByBinAndLicenseCode_Success() {
        testLicense.setIsActive(true);
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.of(testLicense));

        boolean result = licenseService.validateLicenseByBinAndLicenseCode(testLicense.getLicenseCode(), testLicense.getBinNumber());

        assertTrue(result);
    }

    @Test
    public void testValidateLicenseByBinAndLicenseCode_InvalidCode() {
        assertThrows(InvalidLicenseCodeException.class, () -> licenseService.validateLicenseByBinAndLicenseCode("12", testLicense.getBinNumber()));
    }

    @Test
    public void testValidateLicense_Success() {
        testLicense.setIsActive(true);
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(licenseRepository.findByLicenseCode(testLicense.getLicenseCode())).thenReturn(Optional.of(testLicense));

        boolean result = licenseService.validateLicense(testLicense.getLicenseCode(), testLicense.getBinNumber(), testLicense.getNid());

        assertTrue(result);
    }

    @Test
    public void testValidateLicense_InvalidNidOrBin() {
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(false);

        assertThrows(BinOrNidNumberMismatchException.class,
                () -> licenseService.validateLicense(testLicense.getLicenseCode(), testLicense.getBinNumber(), testLicense.getNid()));
    }

    @Test
    public void testValidateBinAndNid_Success() {
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);

        boolean result = licenseService.validateLicense(testLicense.getLicenseCode(), testLicense.getBinNumber(), testLicense.getNid());

        assertTrue(result);
    }

    @Test
    public void testValidateBinAndNid_ErrorFromExternalService() {
        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenThrow(HttpClientErrorException.class);

        assertThrows(RuntimeException.class,
                () -> licenseService.validateLicense(testLicense.getLicenseCode(), testLicense.getBinNumber(), testLicense.getNid()));
    }
}

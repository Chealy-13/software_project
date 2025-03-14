package org.example.software_project.controllers;

import org.example.software_project.Persistence.VehicleDao;
import org.example.software_project.business.User;
import org.example.software_project.business.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @Mock
    private VehicleDao vehicleDao;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCarSuccess() throws IOException {
        User mockUser = new User();
        mockUser.setId(1);
        when(session.getAttribute("currentUser")).thenReturn(mockUser);

        MockMultipartFile mockImageFile = new MockMultipartFile(
                "imageFile", "carBack.jpg", "image/jpeg", "fake-image-content".getBytes());


        Path uploadPath = Paths.get("src/main/resources/static/images/");
        Files.createDirectories(uploadPath);

        String result = vehicleController.addCar(
                        "Alfa Romeo", "Gulietta", 2011, 20000, 15000, "Diesel", "Manual",
                "Sedan", "Fantastic car", "Dublin", mockImageFile, session, redirectAttributes);

        verify(vehicleDao, times(1)).saveVehicle(any(Vehicle.class), eq("car.jpg"));
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Car successfully added!");

        Path filePath = uploadPath.resolve("tesla.png");
        assertEquals(true, Files.exists(filePath));
        assertEquals("redirect:/vehicles", result);
    }

    @Test
    void testAddCar_UserNotLoggedIn() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        String result = vehicleController.addCar(
                "Toyota", "Corolla", 2022, 20000, 15000, "Petrol", "Automatic",
                "Sedan", "A great car", "Dublin", mock(MockMultipartFile.class), session, redirectAttributes);
        verify(vehicleDao, never()).saveVehicle(any(Vehicle.class), anyString());

        assertEquals("redirect:/loginPage", result);
    }

    @Test
    void testAddCar_ImageUploadFails() throws IOException {
        User mockUser = new User();
        mockUser.setId(1);
        when(session.getAttribute("currentUser")).thenReturn(mockUser);
        MockMultipartFile mockImageFile = mock(MockMultipartFile.class);
        when(mockImageFile.getOriginalFilename()).thenReturn("car.jpg");
        when(mockImageFile.getInputStream()).thenThrow(new IOException("Simulated file upload failure"));

        String result = vehicleController.addCar(
                "Tesla", "X", 2021, 20000, 15000, "Petrol", "Automatic",
                "Sedan", "A great car", "Dublin", mockImageFile, session, redirectAttributes);

        verify(vehicleDao, never()).saveVehicle(any(Vehicle.class), anyString());

        assertEquals("redirect:/vehicles?error=imageUploadFailed", result);
    }
}

package org.example.software_project.controllers;

import org.example.software_project.business.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testViewProfileLoggedIn() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testUser");

        when(session.getAttribute("currentUser")).thenReturn(mockUser);

        String viewName = userController.viewProfile(model, session);

        assertEquals("profile", viewName);
        verify(model).addAttribute("user", mockUser);
    }

    @Test
    void testViewProfileNotLoggedIn() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        String viewName = userController.viewProfile(model, session);

        assertEquals("redirect:/loginPage", viewName);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }
}
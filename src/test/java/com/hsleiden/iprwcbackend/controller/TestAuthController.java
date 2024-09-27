package com.hsleiden.iprwcbackend.controller;

import com.hsleiden.iprwcbackend.controllers.AuthController;
import com.hsleiden.iprwcbackend.model.LoginCredentials;
import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.UserRepo;
import com.hsleiden.iprwcbackend.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestAuthController {

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_Return_JWTAndUser_When_LoggingIn_WithValidCredentials() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("test@mail.com");
        credentials.setPassword("Test123!");

        User mockUser = new User();
        mockUser.setEmail("test@mail.com");
        mockUser.setEnabled(true);
        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));

        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken("test@mail.com", "Test123!");
        doAnswer(invocation -> null).when(authManager).authenticate(authInputToken);

        when(jwtUtil.generateToken("test@mail.com")).thenReturn("fake-jwt-token");

        Map<String, Object> response = authController.login(credentials);

        assertEquals("fake-jwt-token", response.get("token"));
        assertEquals(mockUser, response.get("user"));
    }

    @Test
    public void should_ThrowError_When_LoggingIn_WithInvalidCredentials() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("wrongtest@mail.com");
        credentials.setPassword("wrongTest123!");

        when(userRepo.findByEmail("wrongtest@mail.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(credentials);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", exception.getReason());
    }

    @Test
    public void should_ThrowError_When_LoggingIn_WithDisabledUser() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("disabledtest@mail.com");
        credentials.setPassword("disabledTest123!");

        User mockUser = new User();
        mockUser.setEmail("disabledtest@mail.com");
        mockUser.setEnabled(false);  // User is disabled
        when(userRepo.findByEmail("disabledtest@mail.com")).thenReturn(Optional.of(mockUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(credentials);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("USER_NOT_FOUND", exception.getReason());
    }

    @Test
    public void should_ThrowError_When_LoggingIn_WithAuthenticationFailure() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("test@mail.com");
        credentials.setPassword("wrongTest123!");

        User mockUser = new User();
        mockUser.setEmail("test@mail.com");
        mockUser.setEnabled(true);
        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));

        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken("test@mail.com", "wrongTest123!");
        doThrow(new AuthenticationServiceException("Authentication failed")).when(authManager).authenticate(authInputToken);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(credentials);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", exception.getReason());
    }
}

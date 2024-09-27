package com.hsleiden.iprwcbackend.services;

import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.UserRepo;
import com.hsleiden.iprwcbackend.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestAuthorizationService {

    @Mock
    private UserRepo userRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_Return_LoggedInUser_When_UserIsLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("test@mail.com");

        User mockUser = new User();
        mockUser.setEmail("test@mail.com");
        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.of(mockUser));

        User loggedInUser = authorizationService.getLoggedInUser();

        assertNotNull(loggedInUser);
        assertEquals("test@mail.com", loggedInUser.getEmail());

        verify(userRepo, times(2)).findByEmail("test@mail.com");
    }

    @Test
    public void should_Return_Null_When_UserIsNotLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("test@mail.com");

        when(userRepo.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        User loggedInUser = authorizationService.getLoggedInUser();

        assertNull(loggedInUser);

        verify(userRepo, times(1)).findByEmail("test@mail.com");

    }
}

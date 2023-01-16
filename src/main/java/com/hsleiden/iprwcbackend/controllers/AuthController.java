package com.hsleiden.iprwcbackend.controllers;

import com.hsleiden.iprwcbackend.model.LoginCredentials;
import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.UserRepo;
import com.hsleiden.iprwcbackend.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(@RequestBody User user) {
        try {
            // Check if the email already exists
            if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "EMAIL_EXISTS"
                );
            }
            String encodedPass = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);
            user = userRepo.save(user);
            String token = jwtUtil.generateToken(user.getEmail());

            return Map.of(
                    "token", token,
                    "user", user
            );
        } catch (DataIntegrityViolationException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_DETAILS_MISSING");
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody LoginCredentials body) {
        try {
            UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            User user = userRepo.findByEmail(body.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS"));
            if (!user.isEnabled()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
            }
            authManager.authenticate(authInputToken);
            String token = jwtUtil.generateToken(body.getEmail());

            return Map.of(
                    "token", token,
                    "user", user
            );
        } catch (AuthenticationException authExc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }
    }

}

package com.hsleiden.iprwcbackend.controllers;

import com.hsleiden.iprwcbackend.model.User;
import com.hsleiden.iprwcbackend.repository.UserRepo;
import com.hsleiden.iprwcbackend.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    @ResponseBody
    public User[] getAllUsers() {
        return userRepo.findAll().toArray(new User[0]);
    }

    @GetMapping("/me")
    @ResponseBody
    public User getLoggedInUser() {
        return authorizationService.getLoggedInUser();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUserById(@PathVariable(value = "id") UUID id) {
        // Check if user is looking for himself
        if (authorizationService.getLoggedInUser().getId() == id || authorizationService.getLoggedInUser().getRole() == User.Role.ADMIN) {
            return userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
        }
        return null;
    }

    @PostMapping("/{id}")
    @ResponseBody
    public User updateUser(@PathVariable(value = "id") UUID id, @RequestBody User user) {
        if (authorizationService.getLoggedInUser().getId() == id || authorizationService.getLoggedInUser().getRole() == User.Role.ADMIN) {
            User userToUpdate = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
            // Update fields that are given
            if (user.getFirstname() != null) userToUpdate.setFirstname(user.getFirstname());
            if (user.getLastname() != null) userToUpdate.setLastname(user.getLastname());
            if (user.getBirthdate() != null) userToUpdate.setBirthdate(user.getBirthdate());
            if (user.getGender() != null) userToUpdate.setGender(user.getGender());
            if (user.getEmail() != null) userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null) {
                String encodedPass = passwordEncoder.encode(user.getPassword());
                userToUpdate.setPassword(encodedPass);
            }

            return userRepo.save(userToUpdate);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable(value = "id") UUID id) {
        User userToDelete = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        userRepo.delete(userToDelete);
    }
}
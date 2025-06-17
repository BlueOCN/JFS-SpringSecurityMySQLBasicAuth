package com.blueocn.SecurityMySqlDatabase.controller;

import com.blueocn.SecurityMySqlDatabase.data.dto.RegisterRequest;
import com.blueocn.SecurityMySqlDatabase.data.dto.UpdateRequest;
import com.blueocn.SecurityMySqlDatabase.data.entity.UserEntity;
import com.blueocn.SecurityMySqlDatabase.service.AuthorityService;
import com.blueocn.SecurityMySqlDatabase.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;
    private final AuthorityService authorityService;


    @Autowired
    public UserController(UserService userService, AuthorityService authorityService) {
        this.userService = userService;
        this.authorityService = authorityService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        authorityService.registerAuthority(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserEntity>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{username}")
    public ResponseEntity<String> updateUser(@PathVariable("username") String username, @Valid @RequestBody UpdateRequest request) {
        userService.updateUser(username, request);
        authorityService.updateAuthority(username, request);
        return ResponseEntity.ok("User updated successfully.");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
        authorityService.deleteAuthority(username);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserEntity> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

}

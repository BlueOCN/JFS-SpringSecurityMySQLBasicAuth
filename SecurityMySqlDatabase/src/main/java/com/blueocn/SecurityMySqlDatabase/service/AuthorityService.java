package com.blueocn.SecurityMySqlDatabase.service;

import com.blueocn.SecurityMySqlDatabase.data.dto.RegisterRequest;
import com.blueocn.SecurityMySqlDatabase.data.dto.UpdateRequest;
import com.blueocn.SecurityMySqlDatabase.data.entity.AuthorityEntity;
import com.blueocn.SecurityMySqlDatabase.data.repository.AuthorityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
public class AuthorityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityService.class);
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public AuthorityEntity registerAuthority(RegisterRequest request) {
        LOGGER.debug("Attempting to register authority for user '{}'", request.getUsername());
        if (authorityRepository.findByUsername(request.getUsername()).isPresent()) {
            LOGGER.warn("Registration failed - authority for username '{}' already exists", request.getUsername());
            throw new IllegalArgumentException("Username is already taken.");
        }

        AuthorityEntity auth = new AuthorityEntity(request.getAuthority(), request.getUsername());
        AuthorityEntity savedAuth = authorityRepository.save(auth);

        LOGGER.info("Authority '{}' successfully registered for user '{}'", savedAuth.getAuthority(), savedAuth.getUser());
        return savedAuth;
    }

    public AuthorityEntity updateAuthority(String username, UpdateRequest request) {
        LOGGER.debug("Attempting to update authority for user '{}'", username);
        return authorityRepository.findByUsername(username)
                .map(authorityEntity -> {
                    authorityEntity.setUser(request.getUsername());
                    authorityEntity.setAuthority(request.getRole());
                    AuthorityEntity updatedAuth = authorityRepository.save(authorityEntity);
                    LOGGER.info("Authority successfully updated for user '{}'", updatedAuth.getUser());
                    return updatedAuth;
                }).orElseThrow(() -> {
                    LOGGER.error("Failed to update - authority for user '{}' not found", username);
                    return new RuntimeException("Authority was not found.");
                });
    }

    public void deleteAuthority(String username) {
        LOGGER.debug("Attempting to delete authority for user '{}'", username);

        Optional<AuthorityEntity> authority = authorityRepository.findByUsername(username);

        if (authority.isEmpty()) {
            LOGGER.warn("Delete failed - authority for user '{}' not found", username);
            throw new IllegalArgumentException("Authority was not found.");
        }

        authorityRepository.deleteById(authority.get().getId());
        LOGGER.info("Authority successfully deleted for user '{}'", username);
    }

}

package com.dotwavesoftware.importscheduler.features.Auth.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.dotwavesoftware.importscheduler.features.User.repository.UserRepository;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class OAuth2UserService {
    
    private static final Logger logger = Logger.getLogger(OAuth2UserService.class.getName());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public OAuth2UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Process OAuth2 user - create if doesn't exist, update if exists
     * @param email User email from OAuth2
     * @param firstName User first name from OAuth2
     * @param lastName User last name from OAuth2
     * @param provider OAuth2 provider (e.g., "google")
     * @param providerId User ID from OAuth2 provider
     * @return UserEntity
     */
    public UserEntity processOAuth2User(String email, String firstName, String lastName, String provider, String providerId) {
        logger.info("Processing OAuth2 user with email: " + email);
        
        // Check if user already exists
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            logger.info("OAuth2 user already exists: " + email);
            UserEntity user = existingUser.get();
            
            // Update OAuth provider info if it's different
            if (user.getOauthProvider() == null || !user.getOauthProvider().equals(provider)) {
                user.setOauthProvider(provider);
                user.setOauthUserId(providerId);
                userRepository.update(user, user.getId());
                logger.info("Updated OAuth2 provider info for user: " + email);
            }
            
            // Update last login
            userRepository.updateLastLogin(user.getId());
            
            return user;
        }
        
        // Create new user
        logger.info("Creating new OAuth2 user: " + email);
        UserEntity newUser = new UserEntity();
        newUser.setUuid(UUID.randomUUID());
        newUser.setEmail(email);
        newUser.setFirstName(firstName != null ? firstName : "");
        newUser.setLastName(lastName != null ? lastName : "");
        newUser.setOauthProvider(provider);
        newUser.setOauthUserId(providerId);
        
        // Set a random password (won't be used for OAuth login)
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        
        int result = userRepository.save(newUser);
        
        if (result > 0) {
            logger.info("OAuth2 user created successfully: " + email);
            // Fetch the user again to get the ID
            Optional<UserEntity> savedUser = userRepository.findByEmail(email);
            return savedUser.orElse(newUser);
        } else {
            logger.severe("Failed to create OAuth2 user: " + email);
            throw new RuntimeException("Failed to create OAuth2 user");
        }
    }
}


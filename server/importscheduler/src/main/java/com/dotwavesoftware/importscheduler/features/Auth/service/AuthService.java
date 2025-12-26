package com.dotwavesoftware.importscheduler.features.Auth.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.dotwavesoftware.importscheduler.features.User.repository.UserRepository;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.features.Auth.dto.LoginRequestDTO;
import com.dotwavesoftware.importscheduler.features.Auth.dto.LoginResponseDTO;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AuthService {
    
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticate user with email and password
     * @param loginRequest Login credentials
     * @return LoginResponseDTO if successful, null if authentication fails
     */
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        logger.info("Attempting to authenticate user with email: " + loginRequest.getEmail());
        
        // Find user by email
        Optional<UserEntity> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            logger.warning("Authentication failed - user not found with email: " + loginRequest.getEmail());
            return null;
        }
        
        UserEntity user = userOptional.get();
        
        // Verify password
        // Note: For now, comparing plain text. In production, use passwordEncoder.matches()
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            logger.warning("Authentication failed - invalid password for email: " + loginRequest.getEmail());
            return null;
        }
        
        // Update last login timestamp
        userRepository.updateLastLogin(user.getId());
        
        logger.info("User authenticated successfully: " + loginRequest.getEmail());
        
        // Generate a simple token (in production, use JWT)
        String token = UUID.randomUUID().toString();
        

        
        // Create response
        return new LoginResponseDTO(
            user.getUuid(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            token
        );
    }
    
    /**
     * Hash a password using BCrypt
     * @param plainPassword Plain text password
     * @return Hashed password
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Verify a password against a hash
     * @param plainPassword Plain text password
     * @param hashedPassword Hashed password from database
     * @return true if password matches
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}


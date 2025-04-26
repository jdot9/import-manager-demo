package com.dotwavesoftware.importscheduler.features.Auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.dotwavesoftware.importscheduler.features.Auth.service.AuthService;
import com.dotwavesoftware.importscheduler.features.Auth.dto.LoginRequestDTO;
import com.dotwavesoftware.importscheduler.features.Auth.dto.LoginResponseDTO;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint
     * @param loginRequest Login credentials (email and password)
     * @return LoginResponseDTO with user data and token if successful
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("Login request received for email: " + loginRequest.getEmail());
        
        // Validate input
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        
        // Authenticate user
        LoginResponseDTO response = authService.authenticateUser(loginRequest);
        
        if (response == null) {
            logger.warning("Login failed for email: " + loginRequest.getEmail());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        
        logger.info("Login successful for email: " + loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }
}


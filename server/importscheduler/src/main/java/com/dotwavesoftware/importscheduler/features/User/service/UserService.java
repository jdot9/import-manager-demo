package com.dotwavesoftware.importscheduler.features.User.service;

import org.springframework.stereotype.Service;
import com.dotwavesoftware.importscheduler.features.User.repository.UserRepository;
import com.dotwavesoftware.importscheduler.features.User.mapper.UserMapper;
import com.dotwavesoftware.importscheduler.features.User.dto.UserResponseDTO;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, UserMapper userMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    public int createUser(UserEntity user) {
        logger.info("Creating new user with email: " + user.getEmail());

        if (user.getUuid() == null) {
            user.setUuid(UUID.randomUUID());
        }
        
        int rowsAffected = userRepository.save(user);
        
        if (rowsAffected > 0) {
            logger.info("User created successfully. Sending welcome email...");
            // Send welcome email if user has email and first name
            if (user.getEmail() != null && user.getFirstName() != null) {
                emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
            }
        } else {
            logger.warning("Failed to create user with the email address: " + user.getEmail());
        }
        
        return rowsAffected;
    }

    public UserResponseDTO getUserById(Integer id) {
        logger.info("Retrieving user with id: " + id);
        
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            return userMapper.userToUserResponseDTO(userOptional.get());
        } else {
            logger.warning("User not found with id: " + id);
            return null;
        }
    }
    public UserResponseDTO getUserByUUID(UUID uuid) {
        logger.info("Retrieving user with id: " + uuid);
        
        Optional<UserEntity> userOptional = userRepository.findByUUID(uuid);
        
        if (userOptional.isPresent()) {
            return userMapper.userToUserResponseDTO(userOptional.get());
        } else {
            logger.warning("User not found with id: " + uuid);
            return null;
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        logger.info("Retrieving all users");
        
        List<UserEntity> users = userRepository.findAll();
        
        List<UserResponseDTO> transformedUsers = users.stream()
                                                      .map(userMapper::userToUserResponseDTO)
                                                      .collect(Collectors.toList());

        return transformedUsers;
    }

    public int updateUser(UserEntity user, Integer id) {
        logger.info("Updating user with id: " + id);
        
        // Check if user exists
        Optional<UserEntity> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isEmpty()) {
            logger.warning("Cannot update - user not found with id: " + id);
            return 0;
        }
        
        // Preserve immutable fields - don't allow them to be changed via update
        UserEntity existingUser = existingUserOptional.get();
        user.setUuid(existingUser.getUuid());
        user.setCreatedAt(existingUser.getCreatedAt());
        
        // If password is empty/null in the update, keep the existing password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
            logger.info("Preserving existing password (no new password provided)");
        }
        
        logger.info("Preserving immutable fields (userRole, uuid, createdAt) during update");
        
        int rowsAffected = userRepository.update(user, id);
        
        if (rowsAffected > 0) {
            logger.info("User updated successfully with id: " + id);
        } else {
            logger.warning("Failed to update user with id: " + id);
        }
        
        return rowsAffected;
    }

    public int updateUserByUUID(UserEntity user, UUID uuid) {
        logger.info("Updating user with UUID: " + uuid);
        
        // Check if user exists and get their ID
        Optional<UserEntity> existingUserOptional = userRepository.findByUUID(uuid);
        if (existingUserOptional.isEmpty()) {
            logger.warning("Cannot update - user not found with UUID: " + uuid);
            return 0;
        }
        
        UserEntity existingUser = existingUserOptional.get();
        Integer userId = existingUser.getId();
        
        // Preserve immutable fields - don't allow them to be changed via update
        user.setUuid(existingUser.getUuid());
        user.setCreatedAt(existingUser.getCreatedAt());
        
        // If password is empty/null in the update, keep the existing password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
            logger.info("Preserving existing password (no new password provided)");
        }
        
        logger.info("Preserving immutable fields (userRole, uuid, createdAt) during update");
        
        int rowsAffected = userRepository.update(user, userId);
        
        if (rowsAffected > 0) {
            logger.info("User updated successfully with UUID: " + uuid);
        } else {
            logger.warning("Failed to update user with UUID: " + uuid);
        }
        
        return rowsAffected;
    }

    public int deleteUser(Integer id) {
        logger.info("Deleting user with id: " + id);
        
        // Check if user exists
        Optional<UserEntity> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            logger.warning("Cannot delete - user not found with id: " + id);
            return 0;
        }
        
        int rowsAffected = userRepository.deleteById(id);
        
        if (rowsAffected > 0) {
            logger.info("User deleted successfully with id: " + id);
        } else {
            logger.warning("Failed to delete user with id: " + id);
        }
        
        return rowsAffected;
    }

    public void updateLastLogin(Integer id) {
        logger.info("Updating last login for user id: " + id);
        userRepository.updateLastLogin(id);
    }
    
    public Optional<UserEntity> getUserByEmail(String email) {
        logger.info("Retrieving user with email: " + email);
        return userRepository.findByEmail(email);
    }
    
    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    public void sendPasswordResetEmail(String email, String resetLink) {
        logger.info("Sending password reset email to: " + email);
        emailService.sendPasswordResetEmail(email, resetLink);
    }
}

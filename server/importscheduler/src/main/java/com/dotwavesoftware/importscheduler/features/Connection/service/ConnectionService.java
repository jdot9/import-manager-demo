package com.dotwavesoftware.importscheduler.features.Connection.service;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.dotwavesoftware.importscheduler.features.Connection.model.dto.ConnectionDTO;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Connection.repository.ConnectionRepository;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.features.User.repository.UserRepository;

@Service
public class ConnectionService {
    
    private static final Logger logger = Logger.getLogger(ConnectionService.class.getName());
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final HubSpotClient hubSpotClient;
    private final Five9Client five9Client;
    

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository, HubSpotClient hubSpotClient, Five9Client five9Client) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.hubSpotClient = hubSpotClient;
        this.five9Client = five9Client;
    }

    // Get all connections associated with a user's uuid
    public List<ConnectionEntity> getAllConnections(String uuidString) {
        logger.info("Getting all connnections for " + uuidString);
        try {
          UUID uuid = UUID.fromString(uuidString);
          List<ConnectionEntity> connections = connectionRepository.findAllConnectionsByUserUuid(uuid);
          return connections != null ? connections : Collections.emptyList();
        } catch (IllegalArgumentException e) {
           logger.warning("Failed to process uuid string. " + e);
           return Collections.emptyList();
        } catch (Exception e) {
           logger.warning("Failed to get connections. " + e);
           return Collections.emptyList();
        }
    }

    /* 

    public void createConnection(ConnectionEntity connection) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            connectionRepository.save(uuid);
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to process uuid string. " + e);
        } catch (Exception e) {
           logger.warning("Failed to create connection. " + e);
        }
    }
    */

    public int createConnection(ConnectionDTO connectionDTO) {
       if (connectionDTO == null) {
           throw new IllegalArgumentException("Connection data is required");
       }
       
       String name = connectionDTO.getName();
       logger.info("Creating connection for user: " + connectionDTO.getUserUuid());
       logger.info("Connection name: " + name);
       logger.info("Connection description: " + connectionDTO.getDescription());
       logger.info("HubSpot Client ID: " + connectionDTO.getHubspotAccessToken());
       logger.info("Five9 Username: " + connectionDTO.getFive9Username());
       
       if (name == null || name.trim().isEmpty()) {
           throw new IllegalArgumentException("Connection name is required");
       }
       
       // Check if this is a HubSpot connection and test the connection first
       String hubspotAccessToken = connectionDTO.getHubspotAccessToken();
       if (!isNullOrEmpty(hubspotAccessToken)) {
           logger.info("Testing HubSpot connection before saving...");
           Boolean isValid = hubSpotClient.testHubSpotConnection(hubspotAccessToken).block();
           if (isValid == null || !isValid) {
               logger.warning("HubSpot connection test failed. Connection not saved.");
               throw new IllegalArgumentException("Invalid HubSpot access token. Please verify your credentials.");
           }
           logger.info("HubSpot connection test successful.");
       }
       
       // Check if this is a Five9 connection and test the connection first
       String five9Username = connectionDTO.getFive9Username();
       String five9Password = connectionDTO.getFive9Password();
       if (!isNullOrEmpty(five9Username) && !isNullOrEmpty(five9Password)) {
           logger.info("Testing Five9 connection before saving...");
           String credentials = five9Username + ":" + five9Password;
           String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
           Boolean isValid = five9Client.testFive9Connection(base64Credentials).block();
           if (isValid == null || !isValid) {
               logger.warning("Five9 connection test failed. Connection not saved.");
               throw new IllegalArgumentException("Invalid Five9 credentials. Please verify your username and password.");
           }
           logger.info("Five9 connection test successful.");
       }
       
       ConnectionEntity connection = new ConnectionEntity();
       connection.setName(name);
       connection.setDescription(connectionDTO.getDescription() != null ? connectionDTO.getDescription() : "");
       
       // Convert empty strings to null to avoid UNIQUE constraint violations
       connection.setFive9Username(isNullOrEmpty(connectionDTO.getFive9Username()) ? null : connectionDTO.getFive9Username());
       connection.setFive9Password(isNullOrEmpty(connectionDTO.getFive9Password()) ? null : connectionDTO.getFive9Password());
       connection.setHubspotAccessToken(isNullOrEmpty(hubspotAccessToken) ? null : hubspotAccessToken);
       connection.setStatus("DISCONNECTED");
       
       UUID userUuid = UUID.fromString(connectionDTO.getUserUuid());
       UserEntity user = userRepository.findByUUID(userUuid)
                                       .orElseThrow(() -> new RuntimeException("User not found"));
       connection.setUser(user);
       
       // Generate UUID if not already set
       if (connection.getUuid() == null) {
           connection.setUuid(UUID.randomUUID());
       }
       return connectionRepository.save(connection);
    }

    /**
     * Delete a connection by ID
     */
    public int deleteConnection(Integer id) {
        logger.info("Deleting connection with id: " + id);
        return connectionRepository.deleteById(id);
    }

    /**
     * Helper method to check if a string is null or empty
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

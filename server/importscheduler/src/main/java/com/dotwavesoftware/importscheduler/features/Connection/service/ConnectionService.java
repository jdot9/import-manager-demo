package com.dotwavesoftware.importscheduler.features.Connection.service;

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
    

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    // Get all connections associated with a user's uuid
    public List<ConnectionEntity> getAllConnections(String uuidString) {
        logger.info("Getting all connnections for " + uuidString);
        try {
          UUID uuid = UUID.fromString(uuidString);
          return connectionRepository.findAllConnectionsByUserUuid(uuid);
        } catch (IllegalArgumentException e) {
           logger.warning("Failed to process uuid string. " + e);
           return null;
        } catch (Exception e) {
           logger.warning("Failed to get connections. " + e);
           return null;
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
       System.out.println(connectionDTO.getUserUuid());
       ConnectionEntity connection = new ConnectionEntity();
       connection.setName(connectionDTO.getName());
       connection.setDescription(connectionDTO.getDescription());
       UUID userUuid = UUID.fromString(connectionDTO.getUserUuid());
       UserEntity user = userRepository.findByUUID(userUuid)
                                       .orElseThrow(() -> new RuntimeException("User not found"));
       connection.setUser(user); 
       System.out.println(connectionDTO.getUserUuid());
       System.out.println(user.getUuid());
        // Generate UUID if not already set (for Spring Data JDBC)
        if (connection.getUuid() == null) {
            connection.setUuid(UUID.randomUUID());
        }
       return connectionRepository.save(connection);

    }

}

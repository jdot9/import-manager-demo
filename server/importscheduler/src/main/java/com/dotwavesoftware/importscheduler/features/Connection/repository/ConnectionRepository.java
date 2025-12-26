package com.dotwavesoftware.importscheduler.features.Connection.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;


@Repository
public class ConnectionRepository extends BaseRepository<ConnectionEntity> {

    private static final Logger logger = Logger.getLogger(ConnectionRepository.class.getName());

    public ConnectionRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ConnectionEntity connection) {
        String sql = "INSERT INTO connections (uuid, name, description, status, import_id, user_uuid, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        System.out.println(connection.getUser().getUuid());
    
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(connection.getUuid()),
            connection.getName(),
            connection.getDescription(),
            connection.getStatus(),
            connection.getImportEntity() != null ? connection.getImportEntity().getId() : null,
          
            // Error: Column uuid cannot be null. 
            connection.getUser() != null ? ConversionUtil.uuidToBytes(connection.getUser().getUuid()) : null
        );
        
        logger.info("Saving connection to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ConnectionEntity> findAll() {
        String sql = "SELECT * FROM connections";
        List<ConnectionEntity> connections = jdbcTemplate.query(sql, new ConnectionRowMapper());
        logger.info("Retrieving all connections from database. Found: " + connections.size());
        return connections;
    }
/*
     String sql = "SELECT * FROM apis WHERE user_uuid = ?";
        List<ApiEntity> apis = jdbcTemplate.query(sql, new ApiRowMapper(), ConversionUtil.uuidToBytes(userUuid));
        logger.info("Retrieving APIs for user UUID " + userUuid + ". Found: " + apis.size());
        return apis;

*/
   
    public List<ConnectionEntity> findAllConnectionsByUserUuid(UUID uuid) {
        String sql = "SELECT * FROM connections WHERE user_uuid = ?";
        logger.info("Retrieving connection with id " + uuid + " from database.");
        List<ConnectionEntity> connections = jdbcTemplate.query(sql, new ConnectionRowMapper(), ConversionUtil.uuidToBytes(uuid));
        return connections;
    }

    @Override
    public int update(ConnectionEntity connection, Integer id) {
        String sql = "UPDATE connections SET name = ?, description = ?, status = ?, " +
                     "import_id = ?, api_id = ?, user_id = ?, modified_at = NOW() " +
                     "WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            connection.getName(),
            connection.getDescription(),
            connection.getStatus(),
            connection.getImportEntity() != null ? connection.getImportEntity().getId() : null,
            connection.getUser() != null ? connection.getUser().getId() : null,
            id
        );
        
        logger.info("Updating connection with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM connections WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting connection with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find all connections for a specific user
     * @param userId The user ID
     * @return List of connections owned by the user
     */
    public List<ConnectionEntity> findByUserId(Integer userId) {
        String sql = "SELECT * FROM connections WHERE user_id = ?";
        List<ConnectionEntity> connections = jdbcTemplate.query(sql, new ConnectionRowMapper(), userId);
        logger.info("Retrieving connections for user id " + userId + ". Found: " + connections.size());
        return connections;
    }
    
    /**
     * Find all connections for a specific API
     * @param apiId The API ID
     * @return List of connections using the API
     */
    public List<ConnectionEntity> findByApiId(Integer apiId) {
        String sql = "SELECT * FROM connections WHERE api_id = ?";
        List<ConnectionEntity> connections = jdbcTemplate.query(sql, new ConnectionRowMapper(), apiId);
        logger.info("Retrieving connections for api id " + apiId + ". Found: " + connections.size());
        return connections;
    }
    
    /**
     * Find all connections by status
     * @param status The connection status (e.g., "ACTIVE", "INACTIVE")
     * @return List of connections with the specified status
     */
    public List<ConnectionEntity> findByStatus(String status) {
        String sql = "SELECT * FROM connections WHERE status = ?";
        List<ConnectionEntity> connections = jdbcTemplate.query(sql, new ConnectionRowMapper(), status);
        logger.info("Retrieving connections with status '" + status + "'. Found: " + connections.size());
        return connections;
    }
    
    /**
     * Find connection by name
     * @param name The connection name
     * @return Optional containing the connection if found
     */
    public Optional<ConnectionEntity> findByName(String name) {
        String sql = "SELECT * FROM connections WHERE name = ?";
        try {
            logger.info("Retrieving connection with name: " + name);
            ConnectionEntity connection = jdbcTemplate.queryForObject(sql, new ConnectionRowMapper(), name);
            return Optional.ofNullable(connection);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No connection found with name: " + name);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve connection with name " + name + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ConnectionEntity> findById(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}

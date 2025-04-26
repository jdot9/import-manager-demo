package com.dotwavesoftware.importscheduler.features.Mapping.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.features.Mapping.model.entity.ConnectionImportMappingEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ConnectionImportMappingRepository {
    
    private static final Logger logger = Logger.getLogger(ConnectionImportMappingRepository.class.getName());
    private final JdbcTemplate jdbcTemplate;

    public ConnectionImportMappingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Save a new connection import mapping
     * @param entity The entity to save
     * @return Number of rows affected
     */
    public int save(ConnectionImportMappingEntity entity) {
        String sql = "INSERT INTO connection_import_mappings (connection_id, import_id, uuid, field_name, " +
                     "mapping_format_id, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            entity.getId().getConnectionId(),
            entity.getId().getImportId(),
            ConversionUtil.uuidToBytes(entity.getUuid()),
            entity.getFieldName(),
            entity.getMappingFormat() != null ? entity.getMappingFormat().getId() : null
        );
        
        logger.info("Saving connection import mapping to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find all connection import mappings
     * @return List of all mappings
     */
    public List<ConnectionImportMappingEntity> findAll() {
        String sql = "SELECT * FROM connection_import_mappings";
        List<ConnectionImportMappingEntity> mappings = jdbcTemplate.query(sql, new ConnectionImportMappingRowMapper());
        logger.info("Retrieving all connection import mappings from database. Found: " + mappings.size());
        return mappings;
    }

    /**
     * Find by ID - Not applicable for composite keys
     * @param id Not used (composite keys required)
     * @return Empty optional
     * @deprecated Use findByCompositeId(connectionId, importId) instead
     */
    @Deprecated
    public Optional<ConnectionImportMappingEntity> findById(Integer id) {
        // This method is not directly applicable for composite keys
        // Use findByCompositeId instead
        logger.warning("findById(Integer) is not applicable for composite keys. Use findByCompositeId(connectionId, importId) instead.");
        return Optional.empty();
    }
    
    /**
     * Find connection import mapping by composite key
     * @param connectionId The connection ID
     * @param importId The import ID
     * @return Optional containing the mapping if found
     */
    public Optional<ConnectionImportMappingEntity> findByCompositeId(Integer connectionId, Integer importId) {
        String sql = "SELECT * FROM connection_import_mappings WHERE connection_id = ? AND import_id = ?";
        try {
            logger.info("Retrieving connection import mapping with connection_id " + connectionId + 
                       " and import_id " + importId + " from database.");
            ConnectionImportMappingEntity entity = jdbcTemplate.queryForObject(sql, 
                new ConnectionImportMappingRowMapper(), connectionId, importId);
            return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No connection import mapping found with connection_id " + connectionId + 
                          " and import_id " + importId);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve connection import mapping: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Update by ID - Not applicable for composite keys
     * @param entity The entity with updated data
     * @param id Not used (composite keys required)
     * @return 0
     * @deprecated Use updateByCompositeId(entity, connectionId, importId) instead
     */
    @Deprecated
    public int update(ConnectionImportMappingEntity entity, Integer id) {
        // This method is not directly applicable for composite keys
        // Use updateByCompositeId instead
        logger.warning("update(entity, Integer) is not applicable for composite keys. Use updateByCompositeId instead.");
        return 0;
    }
    
    /**
     * Update connection import mapping by composite key
     * @param entity The entity with updated data
     * @param connectionId The connection ID
     * @param importId The import ID
     * @return Number of rows affected
     */
    public int updateByCompositeId(ConnectionImportMappingEntity entity, Integer connectionId, Integer importId) {
        String sql = "UPDATE connection_import_mappings SET field_name = ?, mapping_format_id = ?, " +
                     "modified_at = NOW() WHERE connection_id = ? AND import_id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            entity.getFieldName(),
            entity.getMappingFormat() != null ? entity.getMappingFormat().getId() : null,
            connectionId,
            importId
        );
        
        logger.info("Updating connection import mapping with connection_id " + connectionId + 
                   " and import_id " + importId + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    /**
     * Delete by ID - Not applicable for composite keys
     * @param id Not used (composite keys required)
     * @return 0
     * @deprecated Use deleteByCompositeId(connectionId, importId) instead
     */
    @Deprecated
    public int deleteById(Integer id) {
        // This method is not directly applicable for composite keys
        // Use deleteByCompositeId instead
        logger.warning("deleteById(Integer) is not applicable for composite keys. Use deleteByCompositeId instead.");
        return 0;
    }
    
    /**
     * Delete connection import mapping by composite key
     * @param connectionId The connection ID
     * @param importId The import ID
     * @return Number of rows affected
     */
    public int deleteByCompositeId(Integer connectionId, Integer importId) {
        String sql = "DELETE FROM connection_import_mappings WHERE connection_id = ? AND import_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, connectionId, importId);
        logger.info("Deleting connection import mapping with connection_id " + connectionId + 
                   " and import_id " + importId + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find all mappings for a specific connection
     * @param connectionId The connection ID
     * @return List of mappings for the connection
     */
    public List<ConnectionImportMappingEntity> findByConnectionId(Integer connectionId) {
        String sql = "SELECT * FROM connection_import_mappings WHERE connection_id = ?";
        List<ConnectionImportMappingEntity> mappings = jdbcTemplate.query(sql, 
            new ConnectionImportMappingRowMapper(), connectionId);
        logger.info("Retrieving mappings for connection_id " + connectionId + ". Found: " + mappings.size());
        return mappings;
    }
    
    /**
     * Find all mappings for a specific import
     * @param importId The import ID
     * @return List of mappings for the import
     */
    public List<ConnectionImportMappingEntity> findByImportId(Integer importId) {
        String sql = "SELECT * FROM connection_import_mappings WHERE import_id = ?";
        List<ConnectionImportMappingEntity> mappings = jdbcTemplate.query(sql, 
            new ConnectionImportMappingRowMapper(), importId);
        logger.info("Retrieving mappings for import_id " + importId + ". Found: " + mappings.size());
        return mappings;
    }
    
    /**
     * Find all mappings by mapping format
     * @param mappingFormatId The mapping format ID
     * @return List of mappings with the specified format
     */
    public List<ConnectionImportMappingEntity> findByMappingFormatId(Integer mappingFormatId) {
        String sql = "SELECT * FROM connection_import_mappings WHERE mapping_format_id = ?";
        List<ConnectionImportMappingEntity> mappings = jdbcTemplate.query(sql, 
            new ConnectionImportMappingRowMapper(), mappingFormatId);
        logger.info("Retrieving mappings for mapping_format_id " + mappingFormatId + ". Found: " + mappings.size());
        return mappings;
    }
    
    /**
     * Find mapping by field name for a specific connection and import
     * @param connectionId The connection ID
     * @param importId The import ID
     * @param fieldName The field name
     * @return Optional containing the mapping if found
     */
    public Optional<ConnectionImportMappingEntity> findByFieldName(Integer connectionId, Integer importId, String fieldName) {
        String sql = "SELECT * FROM connection_import_mappings WHERE connection_id = ? AND import_id = ? AND field_name = ?";
        try {
            ConnectionImportMappingEntity entity = jdbcTemplate.queryForObject(sql, 
                new ConnectionImportMappingRowMapper(), connectionId, importId, fieldName);
            return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No mapping found with field_name: " + fieldName);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve mapping by field name: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Delete all mappings for a specific connection
     * @param connectionId The connection ID
     * @return Number of rows affected
     */
    public int deleteByConnectionId(Integer connectionId) {
        String sql = "DELETE FROM connection_import_mappings WHERE connection_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, connectionId);
        logger.info("Deleted all mappings for connection_id " + connectionId + ". Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Delete all mappings for a specific import
     * @param importId The import ID
     * @return Number of rows affected
     */
    public int deleteByImportId(Integer importId) {
        String sql = "DELETE FROM connection_import_mappings WHERE import_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, importId);
        logger.info("Deleted all mappings for import_id " + importId + ". Rows affected: " + rowsAffected);
        return rowsAffected;
    }
}

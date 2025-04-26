package com.dotwavesoftware.importscheduler.features.Import.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ImportRepository extends BaseRepository<ImportEntity> {
    
    private static final Logger logger = Logger.getLogger(ImportRepository.class.getName());

    public ImportRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ImportEntity importEntity) {
        String sql = "INSERT INTO imports (uuid, name, status, email_notification, email, user_id, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(importEntity.getUuid()),
            importEntity.getName(),
            importEntity.getStatus(),
            importEntity.isEmailNotification(),
            importEntity.getEmail(),
            importEntity.getUser() != null ? importEntity.getUser().getId() : null
        );
        
        logger.info("Saving import to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ImportEntity> findAll() {
        String sql = "SELECT * FROM imports";
        List<ImportEntity> imports = jdbcTemplate.query(sql, new ImportRowMapper());
        logger.info("Retrieving all imports from database. Found: " + imports.size());
        return imports;
    }

    @Override
    public Optional<ImportEntity> findById(Integer id) {
        String sql = "SELECT * FROM imports WHERE id = ?";
        try {
            logger.info("Retrieving import with id " + id + " from database.");
            ImportEntity importEntity = jdbcTemplate.queryForObject(sql, new ImportRowMapper(), id);
            return Optional.ofNullable(importEntity);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No import found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve import with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(ImportEntity importEntity, Integer id) {
        String sql = "UPDATE imports SET name = ?, status = ?, email_notification = ?, email = ?, " +
                     "user_id = ?, modified_at = NOW() WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            importEntity.getName(),
            importEntity.getStatus(),
            importEntity.isEmailNotification(),
            importEntity.getEmail(),
            importEntity.getUser() != null ? importEntity.getUser().getId() : null,
            id
        );
        
        logger.info("Updating import with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM imports WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting import with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find all imports for a specific user
     * @param userId The user ID
     * @return List of imports owned by the user
     */
    public List<ImportEntity> findByUserId(Integer userId) {
        String sql = "SELECT * FROM imports WHERE user_id = ?";
        List<ImportEntity> imports = jdbcTemplate.query(sql, new ImportRowMapper(), userId);
        logger.info("Retrieving imports for user id " + userId + ". Found: " + imports.size());
        return imports;
    }
    
    /**
     * Find all imports by status
     * @param status The import status (e.g., "ACTIVE", "PAUSED", "COMPLETED")
     * @return List of imports with the specified status
     */
    public List<ImportEntity> findByStatus(String status) {
        String sql = "SELECT * FROM imports WHERE status = ?";
        List<ImportEntity> imports = jdbcTemplate.query(sql, new ImportRowMapper(), status);
        logger.info("Retrieving imports with status '" + status + "'. Found: " + imports.size());
        return imports;
    }
    
    /**
     * Find all imports with email notifications enabled
     * @return List of imports with email notifications enabled
     */
    public List<ImportEntity> findByEmailNotificationEnabled() {
        String sql = "SELECT * FROM imports WHERE email_notification = true";
        List<ImportEntity> imports = jdbcTemplate.query(sql, new ImportRowMapper());
        logger.info("Retrieving imports with email notifications enabled. Found: " + imports.size());
        return imports;
    }
    
    /**
     * Find import by name
     * @param name The import name
     * @return Optional containing the import if found
     */
    public Optional<ImportEntity> findByName(String name) {
        String sql = "SELECT * FROM imports WHERE name = ?";
        try {
            logger.info("Retrieving import with name: " + name);
            ImportEntity importEntity = jdbcTemplate.queryForObject(sql, new ImportRowMapper(), name);
            return Optional.ofNullable(importEntity);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No import found with name: " + name);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve import with name " + name + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Update import status
     * @param id The import ID
     * @param status The new status
     * @return Number of rows affected
     */
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE imports SET status = ?, modified_at = NOW() WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, id);
        logger.info("Updated status for import id " + id + " to '" + status + "'. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
}

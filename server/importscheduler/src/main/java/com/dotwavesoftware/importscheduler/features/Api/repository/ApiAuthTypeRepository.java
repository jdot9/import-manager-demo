package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiAuthTypeEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ApiAuthTypeRepository extends BaseRepository<ApiAuthTypeEntity> {
    
    private static final Logger logger = Logger.getLogger(ApiAuthTypeRepository.class.getName());

    public ApiAuthTypeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ApiAuthTypeEntity apiAuthType) {
        String sql = "INSERT INTO api_auth_types (uuid, type, created_at) VALUES (?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(apiAuthType.getUuid()),
            apiAuthType.getType()
        );
        
        logger.info("Saving API auth type to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ApiAuthTypeEntity> findAll() {
        String sql = "SELECT * FROM api_auth_types ORDER BY type";
        List<ApiAuthTypeEntity> apiAuthTypes = jdbcTemplate.query(sql, new ApiAuthTypeRowMapper());
        logger.info("Retrieving all API auth types from database. Found: " + apiAuthTypes.size());
        return apiAuthTypes;
    }

    @Override
    public Optional<ApiAuthTypeEntity> findById(Integer id) {
        String sql = "SELECT * FROM api_auth_types WHERE id = ?";
        try {
            logger.info("Retrieving API auth type with id " + id + " from database.");
            ApiAuthTypeEntity apiAuthType = jdbcTemplate.queryForObject(sql, new ApiAuthTypeRowMapper(), id);
            return Optional.ofNullable(apiAuthType);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API auth type found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API auth type with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(ApiAuthTypeEntity apiAuthType, Integer id) {
        String sql = "UPDATE api_auth_types SET type = ?, modified_at = NOW() WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            apiAuthType.getType(),
            id
        );
        
        logger.info("Updating API auth type with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM api_auth_types WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting API auth type with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find API auth type by type name
     * @param type The type name
     * @return Optional containing the API auth type if found
     */
    public Optional<ApiAuthTypeEntity> findByType(String type) {
        String sql = "SELECT * FROM api_auth_types WHERE type = ?";
        try {
            logger.info("Retrieving API auth type with type: " + type);
            ApiAuthTypeEntity apiAuthType = jdbcTemplate.queryForObject(sql, new ApiAuthTypeRowMapper(), type);
            return Optional.ofNullable(apiAuthType);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API auth type found with type: " + type);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API auth type with type " + type + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}


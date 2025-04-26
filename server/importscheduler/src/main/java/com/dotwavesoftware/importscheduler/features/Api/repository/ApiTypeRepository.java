package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiTypeEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ApiTypeRepository extends BaseRepository<ApiTypeEntity> {
    
    private static final Logger logger = Logger.getLogger(ApiTypeRepository.class.getName());

    public ApiTypeRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ApiTypeEntity apiType) {
        String sql = "INSERT INTO api_types (uuid, type, created_at) VALUES (?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(apiType.getUuid()),
            apiType.getType()
        );
        
        logger.info("Saving API type to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ApiTypeEntity> findAll() {
        String sql = "SELECT * FROM api_types ORDER BY type";
        List<ApiTypeEntity> apiTypes = jdbcTemplate.query(sql, new ApiTypeRowMapper());
        logger.info("Retrieving all API types from database. Found: " + apiTypes.size());
        return apiTypes;
    }

    @Override
    public Optional<ApiTypeEntity> findById(Integer id) {
        String sql = "SELECT * FROM api_types WHERE id = ?";
        try {
            logger.info("Retrieving API type with id " + id + " from database.");
            ApiTypeEntity apiType = jdbcTemplate.queryForObject(sql, new ApiTypeRowMapper(), id);
            return Optional.ofNullable(apiType);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API type found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API type with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(ApiTypeEntity apiType, Integer id) {
        String sql = "UPDATE api_types SET type = ?, modified_at = NOW() WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            apiType.getType(),
            id
        );
        
        logger.info("Updating API type with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM api_types WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting API type with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find API type by type name
     * @param type The type name
     * @return Optional containing the API type if found
     */
    public Optional<ApiTypeEntity> findByType(String type) {
        String sql = "SELECT * FROM api_types WHERE type = ?";
        try {
            logger.info("Retrieving API type with type: " + type);
            ApiTypeEntity apiType = jdbcTemplate.queryForObject(sql, new ApiTypeRowMapper(), type);
            return Optional.ofNullable(apiType);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API type found with type: " + type);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API type with type " + type + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}


package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiRestMethodEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ApiRestMethodRepository extends BaseRepository<ApiRestMethodEntity> {
    
    private static final Logger logger = Logger.getLogger(ApiRestMethodRepository.class.getName());

    public ApiRestMethodRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ApiRestMethodEntity apiRestMethod) {
        String sql = "INSERT INTO api_rest_methods (uuid, method, created_at) VALUES (?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(apiRestMethod.getUuid()),
            apiRestMethod.getMethod()
        );
        
        logger.info("Saving API REST method to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ApiRestMethodEntity> findAll() {
        String sql = "SELECT * FROM api_rest_methods ORDER BY method";
        List<ApiRestMethodEntity> apiRestMethods = jdbcTemplate.query(sql, new ApiRestMethodRowMapper());
        logger.info("Retrieving all API REST methods from database. Found: " + apiRestMethods.size());
        return apiRestMethods;
    }

    @Override
    public Optional<ApiRestMethodEntity> findById(Integer id) {
        String sql = "SELECT * FROM api_rest_methods WHERE id = ?";
        try {
            logger.info("Retrieving API REST method with id " + id + " from database.");
            ApiRestMethodEntity apiRestMethod = jdbcTemplate.queryForObject(sql, new ApiRestMethodRowMapper(), id);
            return Optional.ofNullable(apiRestMethod);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API REST method found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API REST method with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(ApiRestMethodEntity apiRestMethod, Integer id) {
        String sql = "UPDATE api_rest_methods SET method = ?, modified_at = NOW() WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            apiRestMethod.getMethod(),
            id
        );
        
        logger.info("Updating API REST method with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM api_rest_methods WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting API REST method with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find API REST method by method name
     * @param method The method name (GET, POST, PUT, DELETE, etc.)
     * @return Optional containing the API REST method if found
     */
    public Optional<ApiRestMethodEntity> findByMethod(String method) {
        String sql = "SELECT * FROM api_rest_methods WHERE method = ?";
        try {
            logger.info("Retrieving API REST method with method: " + method);
            ApiRestMethodEntity apiRestMethod = jdbcTemplate.queryForObject(sql, new ApiRestMethodRowMapper(), method);
            return Optional.ofNullable(apiRestMethod);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API REST method found with method: " + method);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API REST method with method " + method + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}


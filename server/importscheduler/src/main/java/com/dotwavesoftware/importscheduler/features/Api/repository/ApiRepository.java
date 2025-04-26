package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ApiRepository extends BaseRepository<ApiEntity> {
    
    private static final Logger logger = Logger.getLogger(ApiRepository.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ApiEntity api) {
        String sql = "INSERT INTO apis (uuid, name, base_url, logo_url, auth_details, user_id, user_uuid, api_type_id, api_auth_type_id, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        String authDetailsJson = null;
        if (api.getAuthDetails() != null) {
            try {
                authDetailsJson = objectMapper.writeValueAsString(api.getAuthDetails());
            } catch (Exception e) {
                logger.warning("Failed to serialize auth_details to JSON: " + e.getMessage());
            }
        }
        
        final String finalAuthDetailsJson = authDetailsJson;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBytes(1, ConversionUtil.uuidToBytes(api.getUuid()));
            ps.setString(2, api.getName());
            ps.setString(3, api.getBaseUrl());
            ps.setString(4, api.getLogoUrl());
            ps.setString(5, finalAuthDetailsJson);
            if (api.getUser() != null) {
                ps.setInt(6, api.getUser().getId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            if (api.getUserUuid() != null) {
                ps.setBytes(7, ConversionUtil.uuidToBytes(api.getUserUuid()));
            } else {
                ps.setNull(7, java.sql.Types.BINARY);
            }
            if (api.getApiType() != null) {
                ps.setInt(8, api.getApiType().getId());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            if (api.getApiAuthType() != null) {
                ps.setInt(9, api.getApiAuthType().getId());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);
        
        // Set the generated ID on the entity
        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            api.setId(keyHolder.getKey().intValue());
            logger.info("Saving API to database. Rows affected: " + rowsAffected + ", Generated ID: " + api.getId());
        } else {
            logger.info("Saving API to database. Rows affected: " + rowsAffected);
        }
        
        return rowsAffected;
    }

    @Override
    public List<ApiEntity> findAll() {
        String sql = "SELECT * FROM apis";
        List<ApiEntity> apis = jdbcTemplate.query(sql, new ApiRowMapper());
        logger.info("Retrieving all APIs from database. Found: " + apis.size());
        return apis;
    }

    @Override
    public Optional<ApiEntity> findById(Integer id) {
        String sql = "SELECT * FROM apis WHERE id = ?";
        try {
            logger.info("Retrieving API with id " + id + " from database.");
            ApiEntity api = jdbcTemplate.queryForObject(sql, new ApiRowMapper(), id);
            return Optional.ofNullable(api);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(ApiEntity api, Integer id) {
        String sql = "UPDATE apis SET name = ?, base_url = ?, logo_url = ?, auth_details = ?, " +
                     "user_id = ?, user_uuid = ?, api_type_id = ?, api_auth_type_id = ?, modified_at = NOW() " +
                     "WHERE id = ?";
        
        String authDetailsJson = null;
        if (api.getAuthDetails() != null) {
            try {
                authDetailsJson = objectMapper.writeValueAsString(api.getAuthDetails());
            } catch (Exception e) {
                logger.warning("Failed to serialize auth_details to JSON: " + e.getMessage());
            }
        }
        
        int rowsAffected = jdbcTemplate.update(sql,
            api.getName(),
            api.getBaseUrl(),
            api.getLogoUrl(),
            authDetailsJson,
            api.getUser() != null ? api.getUser().getId() : null,
            api.getUserUuid() != null ? ConversionUtil.uuidToBytes(api.getUserUuid()) : null,
            api.getApiType() != null ? api.getApiType().getId() : null,
            api.getApiAuthType() != null ? api.getApiAuthType().getId() : null,
            id
        );
        
        logger.info("Updating API with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM apis WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting API with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    /**
     * Find all APIs for a specific user
     * @param userId The user ID
     * @return List of APIs owned by the user
     */
    public List<ApiEntity> findByUserId(Integer userId) {
        String sql = "SELECT * FROM apis WHERE user_id = ?";
        List<ApiEntity> apis = jdbcTemplate.query(sql, new ApiRowMapper(), userId);
        logger.info("Retrieving APIs for user id " + userId + ". Found: " + apis.size());
        return apis;
    }
    
    /**
     * Find API by name
     * @param name The API name
     * @return Optional containing the API if found
     */
    public Optional<ApiEntity> findByName(String name) {
        String sql = "SELECT * FROM apis WHERE name = ?";
        try {
            logger.info("Retrieving API with name: " + name);
            ApiEntity api = jdbcTemplate.queryForObject(sql, new ApiRowMapper(), name);
            return Optional.ofNullable(api);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API found with name: " + name);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API with name " + name + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Find all APIs for a specific user by user UUID
     * @param userUuid The user UUID
     * @return List of APIs owned by the user
     */
    public List<ApiEntity> findByUserUuid(java.util.UUID userUuid) {
        String sql = "SELECT * FROM apis WHERE user_uuid = ?";
        List<ApiEntity> apis = jdbcTemplate.query(sql, new ApiRowMapper(), ConversionUtil.uuidToBytes(userUuid));
        logger.info("Retrieving APIs for user UUID " + userUuid + ". Found: " + apis.size());
        return apis;
    }

    /**
     * Find API by UUID
     * @param uuid The API UUID
     * @return Optional containing the API if found
     */
    public Optional<ApiEntity> findByUuid(java.util.UUID uuid) {
        String sql = "SELECT * FROM apis WHERE uuid = ?";
        try {
            logger.info("Retrieving API with UUID: " + uuid);
            ApiEntity api = jdbcTemplate.queryForObject(sql, new ApiRowMapper(), ConversionUtil.uuidToBytes(uuid));
            return Optional.ofNullable(api);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API found with UUID: " + uuid);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API with UUID " + uuid + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Delete API by UUID
     * @param uuid The API UUID
     * @return Number of rows affected
     */
    public int deleteByUuid(java.util.UUID uuid) {
        String sql = "DELETE FROM apis WHERE uuid = ?";
        int rowsAffected = jdbcTemplate.update(sql, ConversionUtil.uuidToBytes(uuid));
        logger.info("Deleting API with UUID " + uuid + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
}

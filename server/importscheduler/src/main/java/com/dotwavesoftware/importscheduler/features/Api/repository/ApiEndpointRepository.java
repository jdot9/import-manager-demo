package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEndpointEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ApiEndpointRepository extends BaseRepository<ApiEndpointEntity> {
    
    private static final Logger logger = Logger.getLogger(ApiEndpointRepository.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiEndpointRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(ApiEndpointEntity apiEndpoint) {
        String sql = "INSERT INTO api_endpoints (uuid, name, path, headers, query_parameters, request_body, " +
                     "soap_envelope, soap_action, description, api_id, api_rest_method_id, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        String headerJson = null;
        String queryParameterJson = null;
        
        try {
            if (apiEndpoint.getHeaders() != null) {
                headerJson = objectMapper.writeValueAsString(apiEndpoint.getHeaders());
            }
            if (apiEndpoint.getQueryParameters() != null) {
                queryParameterJson = objectMapper.writeValueAsString(apiEndpoint.getQueryParameters());
            }
        } catch (Exception e) {
            logger.warning("Failed to serialize JSON fields: " + e.getMessage());
        }
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(apiEndpoint.getUuid()),
            apiEndpoint.getName(),
            apiEndpoint.getPath(),
            headerJson,
            queryParameterJson,
            apiEndpoint.getRequestBody(),
            apiEndpoint.getSoapEnvelope(),
            apiEndpoint.getSoapAction(),
            apiEndpoint.getDescription(),
            apiEndpoint.getApi() != null ? apiEndpoint.getApi().getId() : null,
            apiEndpoint.getApiRestMethod() != null ? apiEndpoint.getApiRestMethod().getId() : null
        );
        
        logger.info("Saving API endpoint to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int update(ApiEndpointEntity apiEndpoint, Integer id) {
        String sql = "UPDATE api_endpoints SET name = ?, path = ?, headers = ?, query_parameters = ?, " +
                     "request_body = ?, soap_envelope = ?, soap_action = ?, description = ?, " +
                     "api_id = ?, api_rest_method_id = ?, modified_at = NOW() " +
                     "WHERE id = ?";
        
        String headerJson = null;
        String queryParameterJson = null;
        
        try {
            if (apiEndpoint.getHeaders() != null) {
                headerJson = objectMapper.writeValueAsString(apiEndpoint.getHeaders());
            }
            if (apiEndpoint.getQueryParameters() != null) {
                queryParameterJson = objectMapper.writeValueAsString(apiEndpoint.getQueryParameters());
            }
        } catch (Exception e) {
            logger.warning("Failed to serialize JSON fields: " + e.getMessage());
        }
        
        int rowsAffected = jdbcTemplate.update(sql,
            apiEndpoint.getName(),
            apiEndpoint.getPath(),
            headerJson,
            queryParameterJson,
            apiEndpoint.getRequestBody(),
            apiEndpoint.getSoapEnvelope(),
            apiEndpoint.getSoapAction(),
            apiEndpoint.getDescription(),
            apiEndpoint.getApi() != null ? apiEndpoint.getApi().getId() : null,
            apiEndpoint.getApiRestMethod() != null ? apiEndpoint.getApiRestMethod().getId() : null,
            id
        );
        
        logger.info("Updating API endpoint with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM api_endpoints WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting API endpoint with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<ApiEndpointEntity> findAll() {
        String sql = "SELECT * FROM api_endpoints";
        List<ApiEndpointEntity> apiEndpoints = jdbcTemplate.query(sql, new ApiEndpointRowMapper());
        logger.info("Retrieving all API endpoints from database. Found: " + apiEndpoints.size());
        return apiEndpoints;
    }

    @Override
    public Optional<ApiEndpointEntity> findById(Integer id) {
        String sql = "SELECT * FROM api_endpoints WHERE id = ?";
        try {
            logger.info("Retrieving API endpoint with id " + id + " from database.");
            ApiEndpointEntity apiEndpoint = jdbcTemplate.queryForObject(sql, new ApiEndpointRowMapper(), id);
            return Optional.ofNullable(apiEndpoint);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API endpoint found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API endpoint with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Find all endpoints for a specific API
     * @param apiId The API ID
     * @return List of endpoints for the API
     */
    public List<ApiEndpointEntity> findByApiId(Integer apiId) {
        String sql = "SELECT * FROM api_endpoints WHERE api_id = ?";
        List<ApiEndpointEntity> apiEndpoints = jdbcTemplate.query(sql, new ApiEndpointRowMapper(), apiId);
        logger.info("Retrieving API endpoints for api id " + apiId + ". Found: " + apiEndpoints.size());
        return apiEndpoints;
    }
    
    /**
     * Find endpoint by name within a specific API
     * @param apiId The API ID
     * @param name The endpoint name
     * @return Optional containing the endpoint if found
     */
    public Optional<ApiEndpointEntity> findByApiIdAndName(Integer apiId, String name) {
        String sql = "SELECT * FROM api_endpoints WHERE api_id = ? AND name = ?";
        try {
            logger.info("Retrieving API endpoint for api id " + apiId + " with name: " + name);
            ApiEndpointEntity apiEndpoint = jdbcTemplate.queryForObject(sql, new ApiEndpointRowMapper(), apiId, name);
            return Optional.ofNullable(apiEndpoint);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No API endpoint found for api id " + apiId + " with name: " + name);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve API endpoint: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Delete all endpoints for a specific API
     * @param apiId The API ID
     * @return Number of rows affected
     */
    public int deleteByApiId(Integer apiId) {
        String sql = "DELETE FROM api_endpoints WHERE api_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, apiId);
        logger.info("Deleting API endpoints for api id " + apiId + ". Rows affected: " + rowsAffected);
        return rowsAffected;
    }
}

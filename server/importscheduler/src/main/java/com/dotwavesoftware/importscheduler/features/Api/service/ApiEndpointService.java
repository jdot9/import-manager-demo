package com.dotwavesoftware.importscheduler.features.Api.service;

import org.springframework.stereotype.Service;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiEndpointRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEndpointEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ApiEndpointService {

    private static final Logger logger = Logger.getLogger(ApiEndpointService.class.getName());
    private final ApiEndpointRepository apiEndpointRepository;

    public ApiEndpointService(ApiEndpointRepository apiEndpointRepository) {
        this.apiEndpointRepository = apiEndpointRepository;
    }
    
    /**
     * Create a new API endpoint
     * @param apiEndpoint The API endpoint entity to create
     * @return Number of rows affected
     */
    public int createApiEndpoint(ApiEndpointEntity apiEndpoint) {
        logger.info("Creating new API endpoint: " + apiEndpoint.getName());
        
        // Check if endpoint with same name already exists for this API
        if (apiEndpoint.getApi() != null && apiEndpoint.getName() != null) {
            Optional<ApiEndpointEntity> existingEndpoint = 
                apiEndpointRepository.findByApiIdAndName(
                    apiEndpoint.getApi().getId(), 
                    apiEndpoint.getName()
                );
            if (existingEndpoint.isPresent()) {
                logger.warning("API endpoint with name '" + apiEndpoint.getName() + 
                              "' already exists for this API");
                return 0;
            }
        }
        
        // Generate UUID if not already set (for Spring Data JDBC)
        if (apiEndpoint.getUuid() == null) {
            apiEndpoint.setUuid(UUID.randomUUID());
        }
        
        int rowsAffected = apiEndpointRepository.save(apiEndpoint);
        
        if (rowsAffected > 0) {
            logger.info("API endpoint created successfully: " + apiEndpoint.getName());
        } else {
            logger.warning("Failed to create API endpoint: " + apiEndpoint.getName());
        }
        
        return rowsAffected;
    }
    
    /**
     * Get all API endpoints
     * @return List of all API endpoints
     */
    public List<ApiEndpointEntity> getAllApiEndpoints() {
        logger.info("Retrieving all API endpoints");
        return apiEndpointRepository.findAll();
    }
    
    /**
     * Get API endpoint by ID
     * @param id The API endpoint ID
     * @return Optional containing the API endpoint if found
     */
    public Optional<ApiEndpointEntity> getApiEndpointById(Integer id) {
        logger.info("Retrieving API endpoint with id: " + id);
        return apiEndpointRepository.findById(id);
    }
    
    /**
     * Get all endpoints for a specific API
     * @param apiId The API ID
     * @return List of endpoints for the API
     */
    public List<ApiEndpointEntity> getEndpointsByApiId(Integer apiId) {
        logger.info("Retrieving API endpoints for API id: " + apiId);
        return apiEndpointRepository.findByApiId(apiId);
    }
    
    /**
     * Get endpoint by API ID and name
     * @param apiId The API ID
     * @param name The endpoint name
     * @return Optional containing the endpoint if found
     */
    public Optional<ApiEndpointEntity> getEndpointByApiIdAndName(Integer apiId, String name) {
        logger.info("Retrieving API endpoint with name '" + name + "' for API id: " + apiId);
        return apiEndpointRepository.findByApiIdAndName(apiId, name);
    }
    
    /**
     * Update an existing API endpoint
     * @param apiEndpoint The API endpoint entity with updated data
     * @param id The ID of the API endpoint to update
     * @return Number of rows affected
     */
    public int updateApiEndpoint(ApiEndpointEntity apiEndpoint, Integer id) {
        logger.info("Updating API endpoint with id: " + id);
        
        // Check if API endpoint exists
        Optional<ApiEndpointEntity> existingEndpoint = apiEndpointRepository.findById(id);
        if (existingEndpoint.isEmpty()) {
            logger.warning("Cannot update - API endpoint not found with id: " + id);
            return 0;
        }
        
        int rowsAffected = apiEndpointRepository.update(apiEndpoint, id);
        
        if (rowsAffected > 0) {
            logger.info("API endpoint updated successfully with id: " + id);
        } else {
            logger.warning("Failed to update API endpoint with id: " + id);
        }
        
        return rowsAffected;
    }
    
    /**
     * Delete an API endpoint by ID
     * @param id The API endpoint ID
     * @return Number of rows affected
     */
    public int deleteApiEndpoint(Integer id) {
        logger.info("Deleting API endpoint with id: " + id);
        
        // Check if API endpoint exists
        Optional<ApiEndpointEntity> existingEndpoint = apiEndpointRepository.findById(id);
        if (existingEndpoint.isEmpty()) {
            logger.warning("Cannot delete - API endpoint not found with id: " + id);
            return 0;
        }
        
        int rowsAffected = apiEndpointRepository.deleteById(id);
        
        if (rowsAffected > 0) {
            logger.info("API endpoint deleted successfully with id: " + id);
        } else {
            logger.warning("Failed to delete API endpoint with id: " + id);
        }
        
        return rowsAffected;
    }
    
    /**
     * Delete all endpoints for a specific API
     * @param apiId The API ID
     * @return Number of rows affected
     */
    public int deleteEndpointsByApiId(Integer apiId) {
        logger.info("Deleting all API endpoints for API id: " + apiId);
        
        int rowsAffected = apiEndpointRepository.deleteByApiId(apiId);
        
        if (rowsAffected > 0) {
            logger.info("Deleted " + rowsAffected + " API endpoint(s) for API id: " + apiId);
        } else {
            logger.info("No API endpoints found to delete for API id: " + apiId);
        }
        
        return rowsAffected;
    }
    
    /**
     * Check if an endpoint exists by name for a specific API
     * @param apiId The API ID
     * @param name The endpoint name
     * @return true if endpoint exists
     */
    public boolean endpointExistsByApiIdAndName(Integer apiId, String name) {
        return apiEndpointRepository.findByApiIdAndName(apiId, name).isPresent();
    }
}
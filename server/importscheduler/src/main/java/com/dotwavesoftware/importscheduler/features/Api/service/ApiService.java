package com.dotwavesoftware.importscheduler.features.Api.service;

import org.springframework.stereotype.Service;

import com.dotwavesoftware.importscheduler.features.Api.repository.ApiAuthTypeRepository;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiRepository;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiRestMethodRepository;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiTypeRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.dto.ApiPostRequestDTO;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEndpointEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ApiService {

    private static final Logger logger = Logger.getLogger(ApiService.class.getName());
    private final ApiRepository apiRepository;
    private final ApiTypeRepository apiTypeRepository;
    private final ApiAuthTypeRepository apiAuthTypeRepository;
    private final ApiRestMethodRepository apiRestMethodRepository;
    private final ApiEndpointService apiEndpointService;
    private final S3Service s3Service;

    public ApiService(ApiRepository apiRepository, ApiTypeRepository apiTypeRepository, 
                      ApiAuthTypeRepository apiAuthTypeRepository, ApiRestMethodRepository apiRestMethodRepository,
                      ApiEndpointService apiEndpointService, S3Service s3Service) {
        this.apiRepository = apiRepository;
        this.apiTypeRepository = apiTypeRepository;
        this.apiAuthTypeRepository = apiAuthTypeRepository;
        this.apiRestMethodRepository = apiRestMethodRepository;
        this.apiEndpointService = apiEndpointService;
        this.s3Service = s3Service;
    }

    // Splits ApiPostRequestDTO into ApiEntity and multiple ApiEndpointEntity objects
    public int processApiPostRequestDTO(ApiPostRequestDTO newApi, MultipartFile logoFile) {
        // Create ApiEntity from API details
        ApiEntity api = new ApiEntity();
        // API Initialization 
        api.setName(newApi.getApiName());
        api.setBaseUrl(newApi.getBaseUrl());
        api.setApiType(apiTypeRepository.findById(newApi.getApiTypeId()).orElseThrow(() -> new RuntimeException("API type not found")));
        api.setApiAuthType(apiAuthTypeRepository.findById(newApi.getApiAuthTypeId()).orElseThrow(() -> new RuntimeException("API auth type not found")));
        api.setAuthDetails(newApi.getAuthDetails());
        
        // Upload logo to S3 if provided
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = s3Service.uploadLogo(logoFile);
            api.setLogoUrl(logoUrl);
            logger.info("Logo uploaded to S3: " + logoUrl);
        } else {
            api.setLogoUrl(newApi.getLogoUrl());
        }
        
        if (newApi.getUserUuid() != null && !newApi.getUserUuid().isEmpty()) {
            api.setUserUuid(UUID.fromString(newApi.getUserUuid()));
        }

        // Save the API first (the save method now sets the generated ID on the api entity)
        int apiResult = createApi(api);
        if (apiResult <= 0) {
            logger.warning("Failed to create API");
            return 0;
        }
        
        logger.info("API created with ID: " + api.getId());

        // Process multiple API Endpoints
        int endpointsCreated = 0;
        if (newApi.getApiEndpoints() != null && !newApi.getApiEndpoints().isEmpty()) {
            for (var endpointDTO : newApi.getApiEndpoints()) {
                ApiEndpointEntity apiEndpoint = new ApiEndpointEntity();
                
                // Set the parent API reference (links endpoint to the created API)
                apiEndpoint.setApi(api);
                
                apiEndpoint.setName(endpointDTO.getName());
                apiEndpoint.setPath(endpointDTO.getPath());
                
                // Validate apiRestMethodId before lookup
                Integer restMethodId = endpointDTO.getApiRestMethodId();
                if (restMethodId == null || restMethodId <= 0) {
                    throw new RuntimeException("API endpoint '" + endpointDTO.getName() + "' is missing a valid HTTP method. Please select a method (GET, POST, etc.).");
                }
                apiEndpoint.setApiRestMethod(apiRestMethodRepository.findById(restMethodId)
                    .orElseThrow(() -> new RuntimeException("API REST method with ID " + restMethodId + " not found. Please select a valid HTTP method.")));
                
                apiEndpoint.setHeaders(endpointDTO.getHeaders());
                apiEndpoint.setQueryParameters(endpointDTO.getQueryParameters());
                apiEndpoint.setRequestBody(endpointDTO.getRequestBody());
                apiEndpoint.setSoapEnvelope(endpointDTO.getSoapEnvelope());
                apiEndpoint.setSoapAction(endpointDTO.getSoapAction());
                apiEndpoint.setDescription(endpointDTO.getDescription());

                int endpointResult = apiEndpointService.createApiEndpoint(apiEndpoint);
                if (endpointResult > 0) {
                    endpointsCreated++;
                }
            }
            logger.info("Created " + endpointsCreated + " API endpoints for API ID: " + api.getId());
        }

        return (apiResult > 0) ? 1 : 0;
    }
    
    /**
     * Create a new API
     * @param api The API entity to create
     * @return Number of rows affected
     */
    public int createApi(ApiEntity api) {
        logger.info("Creating new API: " + api.getName());
        
        // Check if API with same name already exists for this user
        if (api.getUser() != null && api.getName() != null) {
            Optional<ApiEntity> existingApi = apiRepository.findByName(api.getName());
            if (existingApi.isPresent()) {
                logger.warning("API with name '" + api.getName() + "' already exists");
                return 0;
            }
        }
        
        // Generate UUID if not already set (for Spring Data JDBC)
        if (api.getUuid() == null) {
            api.setUuid(UUID.randomUUID());
        }
        
        int rowsAffected = apiRepository.save(api);
        
        if (rowsAffected > 0) {
            logger.info("API created successfully: " + api.getName());
        } else {
            logger.warning("Failed to create API: " + api.getName());
        }
        
        return rowsAffected;
    }
    
    /**
     * Get all APIs
     * @return List of all APIs
     */
    public List<ApiEntity> getAllApis() {
        logger.info("Retrieving all APIs");
        return apiRepository.findAll();
    }
    
    /**
     * Get API by ID
     * @param id The API ID
     * @return Optional containing the API if found
     */
    public Optional<ApiEntity> getApiById(Integer id) {
        logger.info("Retrieving API with id: " + id);
        return apiRepository.findById(id);
    }
    
    /**
     * Get APIs by user ID
     * @param userId The user ID
     * @return List of APIs owned by the user
     */
    public List<ApiEntity> getApisByUserId(Integer userId) {
        logger.info("Retrieving APIs for user id: " + userId);
        return apiRepository.findByUserId(userId);
    }
    
    /**
     * Get APIs by user UUID
     * @param userUuid The user UUID
     * @return List of APIs owned by the user
     */
    public List<ApiEntity> getApisByUserUuid(UUID userUuid) {
        logger.info("Retrieving APIs for user UUID: " + userUuid);
        return apiRepository.findByUserUuid(userUuid);
    }
    
    /**
     * Get API by name
     * @param name The API name
     * @return Optional containing the API if found
     */
    public Optional<ApiEntity> getApiByName(String name) {
        logger.info("Retrieving API with name: " + name);
        return apiRepository.findByName(name);
    }

    /**
     * Get API by UUID
     * @param uuid The API UUID
     * @return Optional containing the API if found
     */
    public Optional<ApiEntity> getApiByUuid(UUID uuid) {
        logger.info("Retrieving API with UUID: " + uuid);
        return apiRepository.findByUuid(uuid);
    }
    
    /**
     * Update an existing API
     * @param api The API entity with updated data
     * @param id The ID of the API to update
     * @return Number of rows affected
     */
    public int updateApi(ApiEntity api, Integer id) {
        logger.info("Updating API with id: " + id);
        
        // Check if API exists
        Optional<ApiEntity> existingApi = apiRepository.findById(id);
        if (existingApi.isEmpty()) {
            logger.warning("Cannot update - API not found with id: " + id);
            return 0;
        }
        
        int rowsAffected = apiRepository.update(api, id);
        
        if (rowsAffected > 0) {
            logger.info("API updated successfully with id: " + id);
        } else {
            logger.warning("Failed to update API with id: " + id);
        }
        
        return rowsAffected;
    }
    
    /**
     * Delete an API by UUID
     * @param uuid The API UUID
     * @return Number of rows affected
     */
    public int deleteApiByUuid(UUID uuid) {
        logger.info("Deleting API with UUID: " + uuid);
        
        // Check if API exists
        Optional<ApiEntity> existingApi = apiRepository.findByUuid(uuid);
        if (existingApi.isEmpty()) {
            logger.warning("Cannot delete - API not found with UUID: " + uuid);
            return 0;
        }
        
        int rowsAffected = apiRepository.deleteByUuid(uuid);
        
        if (rowsAffected > 0) {
            logger.info("API deleted successfully with UUID: " + uuid);
        } else {
            logger.warning("Failed to delete API with UUID: " + uuid);
        }
        
        return rowsAffected;
    }
    
    /**
     * Check if an API exists by name
     * @param name The API name
     * @return true if API exists
     */
    public boolean apiExistsByName(String name) {
        return apiRepository.findByName(name).isPresent();
    }
}

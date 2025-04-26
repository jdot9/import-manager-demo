package com.dotwavesoftware.importscheduler.features.Api.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.dotwavesoftware.importscheduler.features.Api.model.dto.ApiPostRequestDTO;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEndpointEntity;
import com.dotwavesoftware.importscheduler.features.Api.service.ApiService;
import com.dotwavesoftware.importscheduler.features.Api.service.ApiEndpointService;
import com.dotwavesoftware.importscheduler.features.Api.service.S3Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApiService apiService;
    private final ApiEndpointService apiEndpointService;
    private final S3Service s3Service;
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());

    public ApiController(ApiService apiService, ApiEndpointService apiEndpointService, S3Service s3Service) {
        this.apiService = apiService;
        this.apiEndpointService = apiEndpointService;
        this.s3Service = s3Service;
    }

    // Endpoint for JSON requests (without file upload)
    @PostMapping(value = "/apis", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> newApiJson(@RequestBody ApiPostRequestDTO newApi) {
        logger.info("Processing new integration request (JSON).");
        int result = apiService.processApiPostRequestDTO(newApi, null);
        logger.info("Response sent.");
        return (result > 0) ? ResponseEntity.status(201).body("New Integration Successfully created.") : ResponseEntity.status(400).body("Failed to create new integration.");
    }

    // Endpoint for multipart requests (with file upload)
    @PostMapping(value = "/apis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> newApiWithLogo(
            @RequestPart("data") ApiPostRequestDTO newApi,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile) {
        logger.info("Processing new integration request (multipart).");
        int result = apiService.processApiPostRequestDTO(newApi, logoFile);
        logger.info("Response sent.");
        return (result > 0) ? ResponseEntity.status(201).body("New Integration Successfully created.") : ResponseEntity.status(400).body("Failed to create new integration.");
    }
    
    @GetMapping("/apis")
    public ResponseEntity<?> getAllApisByUser(@RequestParam String userId) {
        try {
            logger.info("Retrieving APIs for user UUID: " + userId);
            java.util.UUID userUuid = java.util.UUID.fromString(userId);
            java.util.List<com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity> apis = apiService.getApisByUserUuid(userUuid);
            logger.info("Successfully retrieved " + apis.size() + " APIs for user");
            return ResponseEntity.ok(apis);
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid UUID format: " + userId);
            return ResponseEntity.status(400).body("Invalid user UUID format");
        } catch (Exception e) {
            logger.severe("Error retrieving APIs for user: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving APIs: " + e.getMessage());
        }
    }

    @GetMapping("/apis/api")
    public ResponseEntity<?> getApiByUuid(@RequestParam String apiId) {
        try {
            logger.info("Retrieving API by UUID: " + apiId);
            java.util.UUID apiUuid = java.util.UUID.fromString(apiId);
            Optional<ApiEntity> api = apiService.getApiByUuid(apiUuid);
            if (api.isPresent()) {
                logger.info("Successfully retrieved API: " + api.get().getName());
                return ResponseEntity.ok(api.get());
            } else {
                logger.warning("API not found with UUID: " + apiId);
                return ResponseEntity.status(404).body("API not found");
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid UUID format: " + apiId);
            return ResponseEntity.status(400).body("Invalid api UUID format");
        } catch (Exception e) {
            logger.severe("Error retrieving selected API: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving selected API: " + e.getMessage());
        }
    }
    

    @GetMapping("/logos/{fileName}")
    public ResponseEntity<byte[]> getLogo(@PathVariable String fileName) {
        try {
            logger.info("Retrieving logo: " + fileName);
            byte[] logoData = s3Service.getLogo(fileName);
            
            // Determine content type based on file extension
            String contentType = MediaType.IMAGE_JPEG_VALUE;
            if (fileName.toLowerCase().endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = MediaType.IMAGE_GIF_VALUE;
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(logoData);
        } catch (Exception e) {
            logger.severe("Error retrieving logo: " + e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/apis/{apiId}/endpoints")
    public ResponseEntity<?> getEndpointsByApiId(@PathVariable Integer apiId) {
        try {
            logger.info("Retrieving endpoints for API ID: " + apiId);
            java.util.List<ApiEndpointEntity> endpoints = apiEndpointService.getEndpointsByApiId(apiId);
            logger.info("Successfully retrieved " + endpoints.size() + " endpoints for API ID: " + apiId);
            return ResponseEntity.ok(endpoints);
        } catch (Exception e) {
            logger.severe("Error retrieving endpoints for API: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving endpoints: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/apis/{apiId}")
    public ResponseEntity<?> deleteApiByUuid(@PathVariable String apiId) {
        try {
            logger.info("Retrieving API by UUID: " + apiId);
            java.util.UUID apiUuid = java.util.UUID.fromString(apiId);
            Optional<ApiEntity> api = apiService.getApiByUuid(apiUuid);
            if (api.isPresent()) {
                apiService.deleteApiByUuid(apiUuid);
                logger.info("Successfully deleted API: " + api.get().getName());
                return ResponseEntity.status(204).build();
            } else {
                logger.warning("API not found with UUID: " + apiId);
                return ResponseEntity.status(404).body("Cannot delete an API that doesn't exist.");
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid UUID format: " + apiId);
            return ResponseEntity.status(400).body("Invalid api UUID format");
        } catch (Exception e) {
            logger.severe("Error retrieving selected API: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving selected API: " + e.getMessage());
        }
    }


}

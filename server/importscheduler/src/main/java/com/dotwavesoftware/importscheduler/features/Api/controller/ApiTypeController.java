package com.dotwavesoftware.importscheduler.features.Api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiTypeRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiTypeEntity;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class ApiTypeController {
    
    private static final Logger logger = Logger.getLogger(ApiTypeController.class.getName());
    private final ApiTypeRepository apiTypeRepository;

    public ApiTypeController(ApiTypeRepository apiTypeRepository) {
        this.apiTypeRepository = apiTypeRepository;
    }

    /**
     * Get all API types
     * @return List of API types
     */
    @GetMapping("/api-types")
    public ResponseEntity<List<ApiTypeEntity>> getAllApiTypes() {
        logger.info("Retrieving all API types");
        List<ApiTypeEntity> apiTypes = apiTypeRepository.findAll();
        logger.info(apiTypes.size() + " API type(s) retrieved");
        return ResponseEntity.ok(apiTypes);
    }
}


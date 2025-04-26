package com.dotwavesoftware.importscheduler.features.Api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiAuthTypeRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiAuthTypeEntity;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class ApiAuthTypeController {
    
    private static final Logger logger = Logger.getLogger(ApiAuthTypeController.class.getName());
    private final ApiAuthTypeRepository apiAuthTypeRepository;

    public ApiAuthTypeController(ApiAuthTypeRepository apiAuthTypeRepository) {
        this.apiAuthTypeRepository = apiAuthTypeRepository;
    }

    /**
     * Get all API authentication types
     * @return List of API auth types
     */
    @GetMapping("/api-auth-types")
    public ResponseEntity<List<ApiAuthTypeEntity>> getAllApiAuthTypes() {
        logger.info("Retrieving all API auth types");
        List<ApiAuthTypeEntity> apiAuthTypes = apiAuthTypeRepository.findAll();
        logger.info(apiAuthTypes.size() + " API auth type(s) retrieved");
        return ResponseEntity.ok(apiAuthTypes);
    }
}


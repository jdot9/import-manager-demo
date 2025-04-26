package com.dotwavesoftware.importscheduler.features.Api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import com.dotwavesoftware.importscheduler.features.Api.repository.ApiRestMethodRepository;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiRestMethodEntity;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class ApiRestMethodController {
    
    private static final Logger logger = Logger.getLogger(ApiRestMethodController.class.getName());
    private final ApiRestMethodRepository apiRestMethodRepository;

    public ApiRestMethodController(ApiRestMethodRepository apiRestMethodRepository) {
        this.apiRestMethodRepository = apiRestMethodRepository;
    }

    /**
     * Get all API REST methods (HTTP methods)
     * @return List of API REST methods
     */
    @GetMapping("/api-rest-methods")
    public ResponseEntity<List<ApiRestMethodEntity>> getAllApiRestMethods() {
        logger.info("Retrieving all API REST methods");
        List<ApiRestMethodEntity> apiRestMethods = apiRestMethodRepository.findAll();
        logger.info(apiRestMethods.size() + " API REST method(s) retrieved");
        return ResponseEntity.ok(apiRestMethods);
    }
}


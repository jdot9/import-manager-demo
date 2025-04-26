package com.dotwavesoftware.importscheduler.features.Api.model.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiPostRequestDTO {

    // User Details
    private String userUuid;

    // API Details
    private String apiName;
    private String baseUrl;
    private int apiTypeId;
    private int apiAuthTypeId;
    private Map<String, String> authDetails;
    private String logoUrl;

    // API Endpoints (multiple)
    private List<ApiEndpointDTO> apiEndpoints;
}

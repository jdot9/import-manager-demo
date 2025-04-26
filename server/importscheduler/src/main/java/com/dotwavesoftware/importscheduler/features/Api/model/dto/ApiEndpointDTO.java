package com.dotwavesoftware.importscheduler.features.Api.model.dto;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiEndpointDTO {
    private String name;
    private String path;
    private Integer apiRestMethodId;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;
    private String requestBody;
    private String soapEnvelope;
    private String soapAction;
    private String description;
}


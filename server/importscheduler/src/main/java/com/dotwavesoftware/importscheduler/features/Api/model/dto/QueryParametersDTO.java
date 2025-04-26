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
public class QueryParametersDTO {
    private Map<String, String> queryParameters;
}

package com.dotwavesoftware.importscheduler.features.Connection.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionDTO {
    private String name;
    private String description;
    private String userUuid;
}

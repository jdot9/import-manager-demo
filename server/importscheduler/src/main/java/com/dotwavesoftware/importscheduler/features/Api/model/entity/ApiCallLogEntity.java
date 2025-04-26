package com.dotwavesoftware.importscheduler.features.Api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "api_call_logs")
public class ApiCallLogEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="api_endpoint_id")
    private ApiEndpointEntity apiEndpoint;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "error")
    private String error;
}

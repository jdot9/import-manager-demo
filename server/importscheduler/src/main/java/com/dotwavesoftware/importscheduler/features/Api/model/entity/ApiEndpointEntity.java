package com.dotwavesoftware.importscheduler.features.Api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.List;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="api_endpoints")
public class ApiEndpointEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="api_id")
    private ApiEntity api;

    @ManyToOne
    @JoinColumn(name="api_rest_method_id")
    private ApiRestMethodEntity apiRestMethod;

    @OneToMany(mappedBy="apiEndpoint")
    private List<ApiCallLogEntity> apiCallLogs;

    @Column(name="name")
    private String name;
    
    @Column(name="path")
    private String path;

    @Type(JsonType.class)
    @Column(name="headers", columnDefinition = "json")
    private Map<String, String> headers;

    @Type(JsonType.class)
    @Column(name="query_parameters", columnDefinition = "json")
    private Map<String, String> queryParameters;

    @Column(name="request_body")
    private String requestBody;

    @Column(name="soap_envelope")
    private String soapEnvelope;
    
    @Column(name="soap_action")
    private String soapAction;

    @Column(name="description")
    private String description;
}

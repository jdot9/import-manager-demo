package com.dotwavesoftware.importscheduler.features.Api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.UUID;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="apis")
public class ApiEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;
    
    @Column(name="user_uuid", columnDefinition = "BINARY(16)")
    private UUID userUuid;

    @ManyToOne
    @JoinColumn(name="api_type_id")
    private ApiTypeEntity apiType;

    @ManyToOne
    @JoinColumn(name="api_auth_type_id")
    private ApiAuthTypeEntity apiAuthType;

    @OneToMany(mappedBy="api")
    private List<ApiEndpointEntity> apiEndpoints;

    @OneToMany(mappedBy="api")
    private List<ConnectionEntity> connections;
    
    @Column(name="name")
    private String name;

    @Column(name="base_url")
    private String baseUrl;

    @Column(name="logo_url")
    private String logoUrl;

    @Type(JsonType.class)
    @Column(name="auth_details", columnDefinition = "json")
    private Map<String, String> authDetails;
}

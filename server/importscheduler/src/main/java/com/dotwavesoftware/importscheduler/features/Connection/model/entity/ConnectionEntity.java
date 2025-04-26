package com.dotwavesoftware.importscheduler.features.Connection.model.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.features.Mapping.model.entity.ConnectionImportMappingEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;

import jakarta.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="connections")
public class ConnectionEntity extends BaseEntity {

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;
    
    @Column(name="status")
    private String status;

    @ManyToOne
    @JoinColumn(name="import_id")
    private ImportEntity importEntity;

    @ManyToOne
    @JoinColumn(name="api_id")
    private ApiEntity api;

    @ManyToOne
    @JoinColumn(name="user_uuid")
    private UserEntity user;

    @OneToMany(mappedBy="connection")
    private List<ConnectionImportMappingEntity> connectionImportMappings;
}

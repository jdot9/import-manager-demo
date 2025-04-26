package com.dotwavesoftware.importscheduler.features.Api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="api_auth_types")
public class ApiAuthTypeEntity extends BaseEntity {

    @Column(name="type")
    private String type;
   
    @OneToMany(mappedBy="apiAuthType")
    private List<ApiEntity> apis;

}

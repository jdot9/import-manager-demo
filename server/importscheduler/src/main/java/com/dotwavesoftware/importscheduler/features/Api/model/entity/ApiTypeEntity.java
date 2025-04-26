package com.dotwavesoftware.importscheduler.features.Api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="api_types")
public class ApiTypeEntity extends BaseEntity {

    @Column(name="type")
    private String type;

    @OneToMany(mappedBy="apiType")
    private List<ApiEntity> apis;
}

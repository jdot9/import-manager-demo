package com.dotwavesoftware.importscheduler.features.Import.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Mapping.model.entity.ConnectionImportMappingEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;

import jakarta.persistence.OneToMany;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="imports")
public class ImportEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    @OneToMany(mappedBy="importEntity")
    private List<ConnectionEntity> connections;

    @OneToMany(mappedBy="importEntity")
    private List<ImportScheduleEntity> importSchedules;

    @OneToMany(mappedBy="importEntity")
    private List<ConnectionImportMappingEntity> connectionImportMappings;

    @Column(name="name")
    private String name;

    @Column(name="status")
    private String status;

    @Column(name="email_notification")
    private boolean emailNotification;

    @Column(name="email")
    private String email;
}

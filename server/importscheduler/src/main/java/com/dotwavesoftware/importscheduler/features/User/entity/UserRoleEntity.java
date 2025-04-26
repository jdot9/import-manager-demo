package com.dotwavesoftware.importscheduler.features.User.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="user_roles")
public class UserRoleEntity extends BaseEntity {

    @OneToMany(mappedBy="userRole")
    private List<UserEntity> users;

    @Column(name="role")
    private String role;
}

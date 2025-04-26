package com.dotwavesoftware.importscheduler.features.User.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.dotwavesoftware.importscheduler.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.List;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="users")
public class UserEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="user_role_id")
    private UserRoleEntity userRole;

    @OneToMany(mappedBy="user")
    private List<ApiEntity> apis;

    @OneToMany(mappedBy="user")
    private List<ConnectionEntity> connections;

    @OneToMany(mappedBy="user")
    private List<ImportEntity> imports;

    @OneToMany(mappedBy="user")
    private List<UserSecurityQuestionEntity> userSecurityQuestions;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="oauth_provider")
    private String oauthProvider;

    @Column(name="oauth_user_id")
    private String oauthUserId;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="last_login_at")
    private LocalDateTime lastLoginAt;
}

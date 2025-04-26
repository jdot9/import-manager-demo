package com.dotwavesoftware.importscheduler.features.User.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.dotwavesoftware.importscheduler.features.User.entity.UserRoleEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class UserRoleRowMapper implements RowMapper<UserRoleEntity> {
    
    @Override
    public UserRoleEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRoleEntity userRole = new UserRoleEntity();
        
        // Map BaseEntity fields
        userRole.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            userRole.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        userRole.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        userRole.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map UserRoleEntity fields
        userRole.setRole(rs.getString("role"));
        
        return userRole;
    }
}

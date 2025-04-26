package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiAuthTypeEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ApiAuthTypeRowMapper implements RowMapper<ApiAuthTypeEntity> {
    
    @Override
    public ApiAuthTypeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiAuthTypeEntity apiAuthType = new ApiAuthTypeEntity();
        
        // Map BaseEntity fields
        apiAuthType.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            apiAuthType.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        apiAuthType.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        apiAuthType.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ApiAuthTypeEntity fields
        apiAuthType.setType(rs.getString("type"));
        
        return apiAuthType;
    }
}


package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiTypeEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ApiTypeRowMapper implements RowMapper<ApiTypeEntity> {
    
    @Override
    public ApiTypeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiTypeEntity apiType = new ApiTypeEntity();
        
        // Map BaseEntity fields
        apiType.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            apiType.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        apiType.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        apiType.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ApiTypeEntity fields
        apiType.setType(rs.getString("type"));
        
        return apiType;
    }
}


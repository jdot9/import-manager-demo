package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiRestMethodEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ApiRestMethodRowMapper implements RowMapper<ApiRestMethodEntity> {
    
    @Override
    public ApiRestMethodEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiRestMethodEntity apiRestMethod = new ApiRestMethodEntity();
        
        // Map BaseEntity fields
        apiRestMethod.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            apiRestMethod.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        apiRestMethod.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        apiRestMethod.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ApiRestMethodEntity fields
        apiRestMethod.setMethod(rs.getString("method"));
        
        return apiRestMethod;
    }
}


package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiTypeEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiAuthTypeEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ApiRowMapper implements RowMapper<ApiEntity> {
    
    @Override
    public ApiEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiEntity api = new ApiEntity();
        
        // Map BaseEntity fields
        api.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            api.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        api.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        api.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ApiEntity fields
        api.setName(rs.getString("name"));
        api.setBaseUrl(rs.getString("base_url"));
        api.setLogoUrl(rs.getString("logo_url"));
        
        // Note: auth_details JSON column needs to be handled by service layer or special method
        // as JdbcTemplate doesn't automatically deserialize JSON to objects
        
        // Map foreign key relationships (create minimal objects with just IDs)
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            UserEntity user = new UserEntity();
            user.setId(userId);
            api.setUser(user);
        }
        
        // Map user_uuid column
        byte[] userUuidBytes = rs.getBytes("user_uuid");
        if (userUuidBytes != null) {
            api.setUserUuid(ConversionUtil.bytesToUuid(userUuidBytes));
        }
        
        int apiTypeId = rs.getInt("api_type_id");
        if (!rs.wasNull()) {
            ApiTypeEntity apiType = new ApiTypeEntity();
            apiType.setId(apiTypeId);
            api.setApiType(apiType);
        }
        
        int apiAuthTypeId = rs.getInt("api_auth_type_id");
        if (!rs.wasNull()) {
            ApiAuthTypeEntity apiAuthType = new ApiAuthTypeEntity();
            apiAuthType.setId(apiAuthTypeId);
            api.setApiAuthType(apiAuthType);
        }
        
        return api;
    }
}

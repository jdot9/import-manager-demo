package com.dotwavesoftware.importscheduler.features.Connection.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ConnectionRowMapper implements RowMapper<ConnectionEntity> {

    @Override
    public ConnectionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConnectionEntity connection = new ConnectionEntity();
        
        // Map BaseEntity fields
        connection.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            connection.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        connection.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        connection.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ConnectionEntity fields
        connection.setName(rs.getString("name"));
        connection.setDescription(rs.getString("description"));
        connection.setStatus(rs.getString("status"));
        
        // Map foreign key relationships (create minimal objects with just IDs)
        int importId = rs.getInt("import_id");
        if (!rs.wasNull()) {
            ImportEntity importEntity = new ImportEntity();
            importEntity.setId(importId);
            connection.setImportEntity(importEntity);
        }
        
        int apiId = rs.getInt("api_id");
        if (!rs.wasNull()) {
            ApiEntity api = new ApiEntity();
            api.setId(apiId);
            connection.setApi(api);
        }


        // Map user_uuid column
        byte[] userUuidBytes = rs.getBytes("user_uuid");
        if (userUuidBytes != null) {
            connection.setUuid(ConversionUtil.bytesToUuid(userUuidBytes));
        }
        
        /* 
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            UserEntity user = new UserEntity();
            user.setId(userId);
            connection.setUser(user);
        }
        */



        return connection;
    }
}


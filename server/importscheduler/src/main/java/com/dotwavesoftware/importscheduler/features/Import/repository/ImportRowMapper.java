package com.dotwavesoftware.importscheduler.features.Import.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ImportRowMapper implements RowMapper<ImportEntity> {
    
    @Override
    public ImportEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ImportEntity importEntity = new ImportEntity();
        
        // Map BaseEntity fields
        importEntity.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            importEntity.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        importEntity.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        importEntity.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ImportEntity fields
        importEntity.setName(rs.getString("name"));
        importEntity.setStatus(rs.getString("status"));
        importEntity.setEmailNotification(rs.getBoolean("email_notification"));
        importEntity.setEmail(rs.getString("email"));
        
        // Map foreign key relationship
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            UserEntity user = new UserEntity();
            user.setId(userId);
            importEntity.setUser(user);
        }
        
        return importEntity;
    }
}

package com.dotwavesoftware.importscheduler.features.Mapping.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Mapping.model.entity.ConnectionImportMappingEntity;
import com.dotwavesoftware.importscheduler.features.Mapping.model.key.ConnectionImportId;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Import.model.entity.ImportEntity;
import com.dotwavesoftware.importscheduler.features.Mapping.model.entity.MappingFormatEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ConnectionImportMappingRowMapper implements RowMapper<ConnectionImportMappingEntity> {
    
    @Override
    public ConnectionImportMappingEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConnectionImportMappingEntity entity = new ConnectionImportMappingEntity();
        
        // Map composite key
        int connectionId = rs.getInt("connection_id");
        int importId = rs.getInt("import_id");
        ConnectionImportId compositeId = new ConnectionImportId(connectionId, importId);
        entity.setId(compositeId);
        
        // Map UUID
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            entity.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        // Map timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        entity.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        entity.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ConnectionImportMappingEntity fields
        entity.setFieldName(rs.getString("field_name"));
        
        // Map foreign key relationships (set only IDs)
        ConnectionEntity connection = new ConnectionEntity();
        connection.setId(connectionId);
        entity.setConnection(connection);
        
        ImportEntity importEntity = new ImportEntity();
        importEntity.setId(importId);
        entity.setImportEntity(importEntity);
        
        int mappingFormatId = rs.getInt("mapping_format_id");
        if (!rs.wasNull()) {
            MappingFormatEntity mappingFormat = new MappingFormatEntity();
            mappingFormat.setId(mappingFormatId);
            entity.setMappingFormat(mappingFormat);
        }
        
        return entity;
    }
}

package com.dotwavesoftware.importscheduler.features.Api.repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEndpointEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiEntity;
import com.dotwavesoftware.importscheduler.features.Api.model.entity.ApiRestMethodEntity;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

public class ApiEndpointRowMapper implements RowMapper<ApiEndpointEntity> {
    
    @Override
    public ApiEndpointEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiEndpointEntity apiEndpoint = new ApiEndpointEntity();
        
        // Map BaseEntity fields
        apiEndpoint.setId(rs.getInt("id"));
        
        byte[] uuidBytes = rs.getBytes("uuid");
        if (uuidBytes != null) {
            apiEndpoint.setUuid(ConversionUtil.bytesToUuid(uuidBytes));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        apiEndpoint.setCreatedAt(ConversionUtil.toLocalDateTime(createdAt));
        apiEndpoint.setModifiedAt(ConversionUtil.toLocalDateTime(modifiedAt));
        
        // Map ApiEndpointEntity fields
        apiEndpoint.setName(rs.getString("name"));
        apiEndpoint.setPath(rs.getString("path"));
        apiEndpoint.setRequestBody(rs.getString("request_body"));
        apiEndpoint.setSoapAction(rs.getString("soap_action"));
        apiEndpoint.setDescription(rs.getString("description"));
        
        // Note: JSON columns (headers, query_parameters, soap_envelope) need to be handled 
        // by service layer or special method as JdbcTemplate doesn't automatically 
        // deserialize JSON to objects
        
        // Map foreign key relationships (create minimal objects with just IDs)
        int apiId = rs.getInt("api_id");
        if (!rs.wasNull()) {
            ApiEntity api = new ApiEntity();
            api.setId(apiId);
            apiEndpoint.setApi(api);
        }
        
        int apiRestMethodId = rs.getInt("api_rest_method_id");
        if (!rs.wasNull()) {
            ApiRestMethodEntity apiRestMethod = new ApiRestMethodEntity();
            apiRestMethod.setId(apiRestMethodId);
            apiEndpoint.setApiRestMethod(apiRestMethod);
        }
        
        return apiEndpoint;
    }
}

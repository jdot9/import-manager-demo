package com.dotwavesoftware.importscheduler.features.Mapping.model.key;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionImportId implements Serializable {
    
    private int connectionId;
    private int importId;
        
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionImportId that = (ConnectionImportId) o;
        return connectionId == that.connectionId && importId == that.importId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(connectionId, importId);
    }
}

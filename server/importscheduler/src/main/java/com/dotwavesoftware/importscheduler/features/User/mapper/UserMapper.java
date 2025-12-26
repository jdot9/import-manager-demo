package com.dotwavesoftware.importscheduler.features.User.mapper;

import org.springframework.stereotype.Component;
import com.dotwavesoftware.importscheduler.features.User.dto.UserResponseDTO;
import com.dotwavesoftware.importscheduler.features.User.entity.UserEntity;
import com.dotwavesoftware.importscheduler.features.User.entity.UserRoleEntity;

@Component
public class UserMapper {
    
    public UserResponseDTO userToUserResponseDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        
            
        
        return new UserResponseDTO(
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getEmail()
        );
    }
    
    public UserEntity userResponseDTOToUser(UserResponseDTO userResponseDTO) {
        if (userResponseDTO == null) {
            return null;
        }
        
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(userResponseDTO.getFirstName());
        userEntity.setLastName(userResponseDTO.getLastName());
        userEntity.setEmail(userResponseDTO.getEmail());
        
        
        return userEntity;
    }
}

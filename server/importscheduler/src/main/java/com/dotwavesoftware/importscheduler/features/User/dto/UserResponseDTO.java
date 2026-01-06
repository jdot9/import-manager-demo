package com.dotwavesoftware.importscheduler.features.User.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
}

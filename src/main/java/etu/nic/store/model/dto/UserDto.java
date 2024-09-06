package etu.nic.store.model.dto;

import etu.nic.store.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private String matchingPassword;
    private OffsetDateTime archived;


    private String jwtToken;
}

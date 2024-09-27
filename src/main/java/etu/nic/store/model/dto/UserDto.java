package etu.nic.store.model.dto;

import etu.nic.store.model.enums.Role;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private String matchingPassword;
    private OffsetDateTime archived;

    @Override
    public String toString() {
        return "Username: " + username + " Email: " + email + " Role: " + role;
    }

}

package etu.nic.store.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String matchingPassword;
    private OffsetDateTime archived;
}

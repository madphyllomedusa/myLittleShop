package etu.nic.store.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private OffsetDateTime archived;
}

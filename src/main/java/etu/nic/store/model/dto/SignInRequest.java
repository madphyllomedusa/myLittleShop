package etu.nic.store.model.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String identifier; // может быть email или username
    private String password;
}

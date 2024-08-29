package etu.nic.store.model.pojo;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private OffsetDateTime archived;
    private Bucket bucket;
}

package etu.nic.store.model.pojo;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class User implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String password;
    private OffsetDateTime archived;
    private String role;
    private Bucket bucket;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.name;
    }
}

package etu.nic.store.model.pojo;

import etu.nic.store.model.enums.Role;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String password;
    private OffsetDateTime archived;
    private Role role; // Используйте enum Role    private Bucket bucket;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return archived == null; // Если archived не null, то аккаунт считается архивированным
    }

    @Override
    public boolean isAccountNonLocked() {
        return archived == null; // Аналогично
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return archived == null; // Аналогично
    }

    @Override
    public boolean isEnabled() {
        return archived == null; // Аналогично
    }
}

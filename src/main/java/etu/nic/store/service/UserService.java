package etu.nic.store.service;

import etu.nic.store.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Boolean save(UserDto userDto);
}

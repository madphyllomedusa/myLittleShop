package etu.nic.store.service;

import etu.nic.store.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto findUserById(Long userId);
    UserDto loginUser(UserDto userDto);
    UserDto saveUser(UserDto userDto);
    UserDto updateUser(Long userId, UserDto userDto);
    void archiveUser(Long userId);
    void restoreUser(Long userId);
}

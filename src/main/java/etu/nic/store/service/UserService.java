package etu.nic.store.service;

import etu.nic.store.model.dto.JwtAuthenticationResponse;
import etu.nic.store.model.dto.SignInRequest;
import etu.nic.store.model.dto.UserDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto findUserById(Long userId);
    UserDto findUserByUsername(String username);
    UserDto findUserByEmail(String email);
    JwtAuthenticationResponse loginUser(SignInRequest signInRequest);
    UserDto saveUser(UserDto userDto);
    UserDto updateUser(Long userId, UserDto userDto);
    void archiveUser(Long userId);
    void restoreUser(Long userId);

    UserDto giveAdminRole(Long userId);
}

package etu.nic.store.service.impl;

import etu.nic.store.dao.UserDao;
import etu.nic.store.model.dto.UserDto;
import etu.nic.store.model.pojo.User;
import etu.nic.store.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Boolean save(UserDto userDto) {
        if(!Objects.equals(userDto.getPassword(), userDto.getMatchingPassword())){
            throw new RuntimeException("Password is not equals");
        }
        User user = User.builder()
                .name(userDto.getName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .archived(null)
                .role("User")
                .build();

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userDao.findByName(username);
        if(user == null){
            throw new UsernameNotFoundException(username + " not found with, check your UserName");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.get().getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.get().getName(),
                user.get().getPassword(),
                roles
        );
    }
}

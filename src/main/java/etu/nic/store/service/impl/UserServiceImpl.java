package etu.nic.store.service.impl;

import etu.nic.store.dao.UserDao;
import etu.nic.store.model.dto.UserDto;
import etu.nic.store.model.enums.Role;
import etu.nic.store.model.mappers.UserMapper;
import etu.nic.store.model.pojo.User;
import etu.nic.store.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDto findUserById(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto loginUser(UserDto userDto) {
        User user = (User) loadUserByUsername(userDto.getEmail());

        if (user.getArchived() != null) {
            throw new IllegalStateException("User account is archived");
        }

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }


        UserDto loggedInUser = userMapper.toDto(user);
        return loggedInUser;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);
        user.setArchived(null);
        userDao.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userDao.update(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void archiveUser(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setArchived(OffsetDateTime.now());
        userDao.update(user);
    }

    @Override
    @Transactional
    public void restoreUser(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setArchived(null);
        userDao.update(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

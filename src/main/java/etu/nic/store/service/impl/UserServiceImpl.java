package etu.nic.store.service.impl;

import etu.nic.store.config.JwtService;
import etu.nic.store.dao.UserDao;
import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.JwtAuthenticationResponse;
import etu.nic.store.model.dto.SignInRequest;
import etu.nic.store.model.dto.UserDto;
import etu.nic.store.model.enums.Role;
import etu.nic.store.model.mappers.UserMapper;
import etu.nic.store.model.pojo.User;
import etu.nic.store.service.UserService;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Override
    public UserDto findUserById(Long userId) {
        logger.info("Find user by id: {}", userId);
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }
    @Override
    public UserDto findUserByUsername(String username) {
        logger.info("Find user by username: {}", username);
        User user = userDao.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }
    @Transactional
    public UserDto findUserByEmail( String email) {
        logger.info("Find user by email: {}", email);
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public JwtAuthenticationResponse loginUser(SignInRequest signInRequest) {
        String identifier = signInRequest.getIdentifier().toLowerCase();
        String password = signInRequest.getPassword();
        logger.info("Attempting to login user with identifier {}", identifier);

        Optional<User> optionalUser = userDao.findByEmail(identifier);

        if (optionalUser.isEmpty()) {
            optionalUser = userDao.findByName(identifier);
        }

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден с идентификатором: " + identifier);
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        UserDetails userDetails = userMapper.toUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        logger.info("Generated token: {}", token);
        logger.info("User {} successfully logged in", optionalUser.get().getUsername());
        return new JwtAuthenticationResponse(token);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) {
        logger.info("Saving user {}", userDto);
        if (userDao.findByEmail(userDto.getEmail()).isPresent()) {
            logger.info("User already exists with email {}", userDto.getEmail());
            throw new BadRequestException("Email уже используется");
        }

        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            throw new BadRequestException("Пароли не совпадают");
        }

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getRole() == null) {
            userDto.setRole(Role.USER);
        }

        if(userDto.getUsername() == null
                || userDto.getUsername().isEmpty()
                || userDto.getEmail() == null
                || userDto.getEmail().isEmpty()) {
            logger.warn("Some user fields is empty or null {}", userDto );
            throw new BadRequestException("Some user fields is empty or null");

        }
        User user = userMapper.toEntity(userDto);
        User savedUser = userDao.save(user);
        logger.info("Saved user {}", savedUser);
        return userMapper.toDto(savedUser);
    }


    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        logger.info("Updating user with id {}", userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setUsername(userDto.getUsername());
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
        logger.info("Archiving user with id {}", userId);
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var now = OffsetDateTime.now();
        user.setArchived(now);
        userDao.update(user);
        logger.info("Successfully archived user with id {}, with time {}", userId,now);
    }

    @Override
    @Transactional
    public void restoreUser(Long userId) {
        logger.info("Restoring user with id {}", userId);
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setArchived(null);
        userDao.update(user);
        logger.info("Successfully restored user with id {}", userId);
    }

    @Override
    @Transactional
    public UserDto giveAdminRole(Long userId) {
        logger.warn("Giving admin role for user with id {}", userId);
        User user = userDao.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setRole(Role.ADMIN);
        userDao.update(user);
        logger.warn("Successfully gived admin role for user {}", user.getUsername());
        return userMapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return userMapper.toUserDetails(user);
    }
}

package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.UserDto;
import etu.nic.store.model.enums.Role;
import etu.nic.store.model.pojo.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.Collections;

@Component
public class UserMapper implements RowMapper<User> {

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName().toLowerCase());
        userDto.setEmail(user.getEmail().toLowerCase());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());
        userDto.setArchived(user.getArchived());
        return userDto;
    }

    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName().toLowerCase());
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        user.setArchived(userDto.getArchived());
        return user;
    }

    public UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail().toLowerCase(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }


    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));

        Timestamp archivedTimestamp = rs.getTimestamp("archived");
        if (archivedTimestamp != null) {
            user.setArchived(archivedTimestamp.toInstant().atOffset(ZoneOffset.UTC));
        } else {
            user.setArchived(null);
        }

        return user;
    }

}



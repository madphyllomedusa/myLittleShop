package etu.nic.store.dao.impl;

import etu.nic.store.dao.UserDao;
import etu.nic.store.model.pojo.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        Map<String, Object> params = Map.of("email", email);

        return jdbcTemplate.query(sql, params, new UserRowMapper())
                .stream()
                .findFirst();
    }
    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = :name";
        Map<String, Object> params = Map.of("name", name);

        return jdbcTemplate.query(sql, params, new UserRowMapper())
                .stream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (:name, :email, :password, :role) RETURNING id";
        Map<String, Object> params = Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "password", user.getPassword(),
                "role", user.getRole()
        );
        Long id = jdbcTemplate.queryForObject(sql, params, Long.class);
        user.setId(id);
        return user;
    }

    private  class UserRowMapper implements RowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }
    }
}

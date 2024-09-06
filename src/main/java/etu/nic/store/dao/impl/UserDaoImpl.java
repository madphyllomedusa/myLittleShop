package etu.nic.store.dao.impl;

import etu.nic.store.dao.UserDao;
import etu.nic.store.model.mappers.UserMapper;
import etu.nic.store.model.pojo.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserMapper userMapper;


    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);

        List<User> users = namedParameterJdbcTemplate.query(sql, params, userMapper);

        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id AND archived IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<User> users = namedParameterJdbcTemplate.query(sql, params, userMapper);

        return users.stream().findFirst();
    }


    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, password, role, archived)" +
                " VALUES (:name, :email, :password, :role, :archived)";

        // Параметры для запроса
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole().name())  // Преобразуем enum Role в строку
                .addValue("archived", user.getArchived());

        // Используем KeyHolder для получения сгенерированного идентификатора
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        // Получаем сгенерированный id и присваиваем его объекту User
        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);

        // Возвращаем обновленный объект User
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET name = :name, email = :email, password =:password, role=:role, archived=:archived";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", user.getName());
        params.addValue("email", user.getEmail());
        params.addValue("password", user.getPassword());
        params.addValue("role", user.getRole().name());
        params.addValue("archived", user.getArchived());

        Long id = namedParameterJdbcTemplate.queryForObject(sql,params, Long.class);
        user.setId(id);
        namedParameterJdbcTemplate.update(sql, params);
        return user;
    }

}

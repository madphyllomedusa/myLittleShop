package etu.nic.store.dao;

import etu.nic.store.model.pojo.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
    Optional<User> findByName(String username);
    User save(User user);
    User update(User user);
}

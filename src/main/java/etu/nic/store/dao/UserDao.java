package etu.nic.store.dao;

import etu.nic.store.model.pojo.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    User save(User user);
}

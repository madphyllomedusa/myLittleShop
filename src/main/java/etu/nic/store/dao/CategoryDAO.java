package etu.nic.store.dao;

import etu.nic.store.model.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Optional<Category> findByTitle(String title);
    Category save(Category category);
    Category update(Category category);
    void deleteById(Long id);
}

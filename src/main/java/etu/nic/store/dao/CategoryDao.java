package etu.nic.store.dao;

import etu.nic.store.model.pojo.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Optional<Category> findByTitle(String title);
    Category save(Category category);
    void addProductToCategory(Long id, Long productId);
    void removeProductsFromCategory(Long id);
    Category update(Category category);
    void deleteById(Long id);
}

package etu.nic.store.dao;

import etu.nic.store.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    List<Product> findAllProducts();
    Optional<Product> findById(Long id);
    Optional<Product> findProductByIdAndDeleted(Long id);
    void addCategoryToProduct(Long productId, Long categoryId);
    void removeCategoryFromProduct(Long productId, Long categoryId);
    Product save(Product product);
    Product update(Product product);
    void deleteById(Long id);
}

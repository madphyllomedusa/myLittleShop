package etu.nic.store.dao;

import etu.nic.store.model.dto.ProductDto;
import etu.nic.store.model.pojo.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    List<Product> findAllProducts();
    Optional<Product> findById(Long id);
    Optional<Product> findDeletedProductById(Long id);
    void addCategoryToProduct(Long productId, Long categoryId);
    void removeCategoryFromProduct(Long productId, Long categoryId);
    Product save(Product product);
    Product update(Product product);
    void deleteById(Long id);
    List<Product> getProductsByCategoryId(Long categoryId);

}

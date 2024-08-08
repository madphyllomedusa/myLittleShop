package etu.nic.store.service;

import etu.nic.store.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAllProducts();
    Product findProductById(long id);
    Product saveProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(long id);
}

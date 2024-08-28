package etu.nic.store.service;

import etu.nic.store.model.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> findAllProducts();
    ProductDto findProductById(Long id);
    ProductDto saveProduct(ProductDto productDTO);
    ProductDto updateProduct(Long id, ProductDto productDTO);
    void deleteProduct(Long id);
}
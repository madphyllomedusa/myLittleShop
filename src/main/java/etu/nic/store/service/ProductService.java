package etu.nic.store.service;

import etu.nic.store.model.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO findProductById(Long id);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id,ProductDTO productDTO);
    void deleteProduct(Long id);
}
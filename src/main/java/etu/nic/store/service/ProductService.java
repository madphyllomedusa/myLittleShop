package etu.nic.store.service;

import etu.nic.store.model.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO findProductById(long id);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(long id, ProductDTO productDTO);
    void deleteProduct(long id);
}
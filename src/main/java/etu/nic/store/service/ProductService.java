package etu.nic.store.service;

import etu.nic.store.model.dto.ProductCreateDTO;
import etu.nic.store.model.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDTO> findAllProducts();
    ProductDTO findProductById(long id);
    ProductDTO saveProduct(ProductCreateDTO productCreateDTO);
    ProductDTO updateProduct(long id, ProductCreateDTO productCreateDTO);
    void deleteProduct(long id);
}
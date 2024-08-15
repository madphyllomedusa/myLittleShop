package etu.nic.store.service.Impl;

import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.Product;
import etu.nic.store.model.mappers.ProductMapper;
import etu.nic.store.repository.ProductRepository;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Primary
public class ProductServiceDBImpl implements ProductService {
    private static Logger logger = Logger.getLogger(ProductServiceDBImpl.class.getName());

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(productMapper::toDto).orElse(null);
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        logger.info("Product saved: " + savedProduct.toString());
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(long id, ProductDTO productCreateDTO) {
        Product product = productMapper.toEntity(productCreateDTO);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated: " + updatedProduct.toString());
        return productMapper.toDto(updatedProduct);
    }


    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

}
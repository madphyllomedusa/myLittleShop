package etu.nic.store.service.Impl;

import etu.nic.store.model.dto.ProductCreateDTO;
import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.Product;
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

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::convertToDTO).orElse(null);
    }

    @Override
    public ProductDTO saveProduct(ProductCreateDTO productCreateDTO) {
        Product product = convertToEntity(productCreateDTO);
        Product savedProduct = productRepository.save(product);
        logger.info("Product saved: " + savedProduct.toString());
        return convertToDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(long id, ProductCreateDTO productCreateDTO) {
        Product product = convertToEntity(productCreateDTO);
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated: " + updatedProduct.toString());
        return convertToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setPrice(product.getPrice());
        return dto;
    }

    private Product convertToEntity(ProductCreateDTO dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setPrice(dto.getPrice());
        return product;
    }
}
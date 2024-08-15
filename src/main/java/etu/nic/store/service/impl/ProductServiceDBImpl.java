package etu.nic.store.service.impl;

import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.mappers.ProductMapper;
import etu.nic.store.repository.ProductRepository;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
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
        return productRepository.findAllByDeletedTimeIsNull()
                .stream()
                .map(productMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(long id) {
        return productRepository.findByIdAndDeletedTimeIsNull(id)
                .map(productMapper.INSTANCE::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        if (productDTO.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be null");
        }

        try {
            logger.info("Received: " + productDTO);
            logger.info("Entity: "+productMapper.toEntity(productDTO));
            Product savedProduct = productRepository.save(productMapper.toEntity(productDTO));

            logger.info("Product saved: " + savedProduct.toString());
            return productMapper.toDto(savedProduct);
        } catch (Exception e) {
            logger.warning("Failed to save product: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save product");
        }
    }



    @Override
    public ProductDTO updateProduct(long id, ProductDTO productCreateDTO) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        try {
            Product product = productMapper.toEntity(productCreateDTO);
            product.setId(id);
            Product updatedProduct = productRepository.save(product);
            logger.info("Product updated: " + updatedProduct.toString());
            return productMapper.toDto(updatedProduct);
        } catch (Exception e) {
            logger.severe("Failed to update product: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product");
        }
    }

    @Override
    public void deleteProduct(long id) {
        Product product = productRepository.findByIdAndDeletedTimeIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        product.setDeletedTime(LocalDateTime.now());  // Мягкое удаление
        productRepository.save(product);
        logger.info("Product softly deleted: " + product.toString());
    }

}

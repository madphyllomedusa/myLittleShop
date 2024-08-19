package etu.nic.store.service.impl;

import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.InternalServerErrorException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.mappers.ProductMapper;
import etu.nic.store.repository.CategoryRepository;
import etu.nic.store.repository.ProductRepository;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAllByDeletedTimeIsNull();

        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }
        return productRepository.findByIdAndDeletedTimeIsNull(id)
                .map(productMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        if (productDTO.getPrice().intValue() <= 0) {
            logger.error("Input price unavailable : {}", productDTO.getPrice());
            throw new BadRequestException("Price must be greater than null");
        }

        try {
            Product product = productMapper.toEntity(productDTO);
            if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
                Set<Category> categories = new HashSet<>(categoryRepository.findAllById(productDTO.getCategoryIds()));
                product.setCategories(categories);
            }
            product.setDeletedTime(null);
            Product savedProduct = productRepository.save(product);
            logger.info("Product saved: {}", savedProduct);
            return productMapper.toDTO(savedProduct);
        } catch (Exception e) {
            logger.error("Failed to save product: {}", e.getMessage());
            throw new BadRequestException("Failed to save product");
        }
    }

    @Override
    public ProductDTO updateProduct(long id, ProductDTO productCreateDTO) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found");
        }
        try {
            Product product = productMapper.toEntity(productCreateDTO);
            product.setId(id);
            if (productCreateDTO.getCategoryIds() != null && !productCreateDTO.getCategoryIds().isEmpty()) {
                Set<Category> categories = new HashSet<>(categoryRepository.findAllById(productCreateDTO.getCategoryIds()));
                product.setCategories(categories);
            }
            Product updatedProduct = productRepository.save(product);
            logger.info("Product updated: {}", updatedProduct);
            return productMapper.toDTO(updatedProduct);
        } catch (Exception e) {
            logger.error("Failed to update product: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to update product");
        }
    }



    @Override
    public void deleteProduct(long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getDeletedTime() != null && productRepository.existsById(id) ) {
            logger.warn("Product is already deleted, deleted time: {}", product.getDeletedTime());
            throw new NotFoundException("Product is already deleted");
        }

        product.setDeletedTime(LocalDateTime.now());
        productRepository.save(product);
        logger.info("Product softly deleted: {}" , product);
    }

}

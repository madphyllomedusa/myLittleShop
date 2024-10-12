package etu.nic.store.service.impl;

import etu.nic.store.dao.impl.ProductDaoImpl;
import etu.nic.store.dao.impl.CategoryDaoImpl;
import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.InternalServerErrorException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.ProductDto;
import etu.nic.store.model.pojo.Product;
import etu.nic.store.model.pojo.Category;
import etu.nic.store.model.mappers.ProductMapper;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDaoImpl productDAO;
    private final CategoryDaoImpl categoryDAO;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products = productDAO.findAllProducts();
        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto findProductById(Long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }
        return productDAO.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Override
    @Transactional
    public ProductDto saveProduct(ProductDto productDTO) {
        if (productDTO.getPrice().intValue() <= 0) {
            logger.error("Input price unavailable: {}", productDTO.getPrice());
            throw new BadRequestException("Price must be greater than zero");
        }

        logger.info("Received from client {}", productDTO);

        Set<Category> categories = new HashSet<>();
        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            categories = productDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryDAO.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Category not found")))
                    .collect(Collectors.toSet());
        }

        Product product = productMapper.toEntity(productDTO,categories);
        product.setDeletedTime(null);

        Product savedProduct = productDAO.save(product);

        for (Category category : product.getCategories()) {
            productDAO.addCategoryToProduct(product.getId(), category.getId());
        }

        logger.info("Product saved: {}", savedProduct);
        return productMapper.toDTO(savedProduct);
    }


    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDTO) {
        if (productDAO.findById(id).isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        try {
            Set<Category> categories = productDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryDAO.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Category not found")))
                    .collect(Collectors.toSet());

            Product product = productMapper.toEntity(productDTO,categories);
            product.setId(id);
            Product updatedProduct = productDAO.update(product);
            logger.info("Product updated: {}", updatedProduct);
            return productMapper.toDTO(updatedProduct);
        } catch (Exception e) {
            logger.error("Failed to update product: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to update product");
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }

        Product product = productDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getDeletedTime() != null) {
            logger.warn("Product is already deleted, deleted time: {}", product.getDeletedTime());
            throw new NotFoundException("Product is already deleted");
        }

        productDAO.deleteById(id);
        logger.info("Product softly deleted with ID: {}", id);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        if (categoryId <= 0) {
            logger.error("Invalid category ID: {}", categoryId);
            throw new BadRequestException("Invalid category ID");
        }

        List<Product> products= productDAO.getProductsByCategoryId(categoryId);

        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}

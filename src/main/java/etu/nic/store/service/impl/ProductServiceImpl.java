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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDaoImpl productDao;
    private final CategoryDaoImpl categoryDao;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products = productDao.findAllProducts();
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
        return productDao.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Override
    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
        if (productDto.getPrice().intValue() <= 0) {
            logger.error("Input price unavailable: {}", productDto.getPrice());
            throw new BadRequestException("Price must be greater than zero");
        }

        logger.info("Received from client {}", productDto);

        Set<Category> categories = new HashSet<>();
        if (productDto.getCategoryIds() != null && !productDto.getCategoryIds().isEmpty()) {
            categories = productDto.getCategoryIds().stream()
                    .map(categoryId -> categoryDao.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Category not found")))
                    .collect(Collectors.toSet());
        }

        Product product = productMapper.toEntity(productDto,categories);
        product.setDeletedTime(null);

        Product savedProduct = productDao.save(product);

        for (Category category : product.getCategories()) {
            productDao.addCategoryToProduct(product.getId(), category.getId());
        }
        if (productDto.getImageUrls() != null && !productDto.getImageUrls().isEmpty()) {
            productDao.addImageToProduct(savedProduct.getId(), productDto.getImageUrls());
        }

        logger.info("Product saved: {}", savedProduct);
        return productMapper.toDTO(savedProduct);
    }


    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDTO) {
        if (productDao.findById(id).isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        try {
            Set<Category> categories = productDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryDao.findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Category not found")))
                    .collect(Collectors.toSet());

            Product product = productMapper.toEntity(productDTO,categories);
            product.setId(id);
            Product updatedProduct = productDao.update(product);
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

        Product product = productDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getDeletedTime() != null) {
            logger.warn("Product is already deleted, deleted time: {}", product.getDeletedTime());
            throw new NotFoundException("Product is already deleted");
        }

        productDao.deleteById(id);
        logger.info("Product softly deleted with ID: {}", id);
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        if (categoryId <= 0) {
            logger.error("Invalid category ID: {}", categoryId);
            throw new BadRequestException("Invalid category ID");
        }

        List<Product> products= productDao.getProductsByCategoryId(categoryId);

        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addImageToProduct(Long productId, List<String> imageUrls) {
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getImageUrls() == null) {
            product.setImageUrls(new ArrayList<>());
        }
        product.getImageUrls().addAll(imageUrls);

        productDao.update(product);
    }

}

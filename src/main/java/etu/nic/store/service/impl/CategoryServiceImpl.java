package etu.nic.store.service.impl;

import etu.nic.store.dao.CategoryDao;
import etu.nic.store.dao.impl.CategoryDaoImpl;
import etu.nic.store.dao.impl.ProductDaoImpl;
import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.CategoryDto;
import etu.nic.store.model.pojo.Category;
import etu.nic.store.model.pojo.Product;
import etu.nic.store.model.mappers.CategoryMapper;
import etu.nic.store.service.CategoryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryDaoImpl categoryDAO;
    private final ProductDaoImpl productDAO;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategoryChildren(Long id) {
        if (id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }

        List<Category> categories= categoryDAO.findCategoryChildren(id);

        if (categories.isEmpty()) {
            logger.warn("Categories list is empty {}", id);
            return Collections.emptyList();
        }
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto saveCategory(CategoryDto categoryDTO) {
        if (categoryDTO == null) {
            logger.error("CategoryDTO is null");
            throw new BadRequestException("CategoryDTO is null");
        }
        logger.info("Received from client {}", categoryDTO);

        Set<Product> products = new HashSet<>();
        if (categoryDTO.getProductIDs() != null && !categoryDTO.getProductIDs().isEmpty()) {
            products = categoryDTO.getProductIDs().stream()
                    .map(productId -> productDAO.findById(productId)
                            .orElseThrow(() -> new NotFoundException("Product not found")))
                    .collect(Collectors.toSet());
        }

        Category category = categoryMapper.toEntity(categoryDTO,products);
        category.setProducts(products);

        Category savedCategory = categoryDAO.save(category);
        for (Product product : products) {
            categoryDAO.addProductToCategory(savedCategory.getId(), product.getId());
        }

        logger.info("Category saved successfully: {}", savedCategory);
        return categoryMapper.toDTO(savedCategory);
    }


    @Override
    public CategoryDto findCategoryById(Long id) {
        if (id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }
        return categoryDAO.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public CategoryDto findCategoryByTitle(String title) {
        if (title == null) {
            logger.error("Invalid category title: {}", title);
            throw new BadRequestException("Invalid category title");
        }
        return categoryDAO.findByTitle(title)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDTO) {
        if (id == null || id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }

        Category existingCategory = categoryDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        existingCategory.setTitle(categoryDTO.getTitle());
        existingCategory.setDescription(categoryDTO.getDescription());

        Set<Product> products = new HashSet<>();
        if (categoryDTO.getProductIDs() != null && !categoryDTO.getProductIDs().isEmpty()) {
            products = categoryDTO.getProductIDs().stream()
                    .map(productId -> productDAO.findById(productId)
                            .orElseThrow(() -> new NotFoundException("Product not found")))
                    .collect(Collectors.toSet());
        }
        existingCategory.setProducts(products);

        Category updatedCategory = categoryDAO.update(existingCategory);

        categoryDAO.removeProductsFromCategory(id);
        for (Product product : products) {
            categoryDAO.addProductToCategory(id, product.getId());
        }

        logger.info("Category updated successfully: {}", updatedCategory);
        return categoryMapper.toDTO(updatedCategory);
    }


    @Override
    public List<CategoryDto> findAllCategories() {
        List<Category> categories = categoryDAO.findAll();
        if (categories.isEmpty()) {
            logger.error("No categories found");
            throw new NotFoundException("No categories found");
        }
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        categoryDAO.deleteById(id);
    }
}

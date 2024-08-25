package etu.nic.store.service.impl;

import etu.nic.store.dao.impl.CategoryDAOImpl;
import etu.nic.store.dao.impl.ProductDAOImpl;
import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.CategoryDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.mappers.CategoryMapper;
import etu.nic.store.service.CategoryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryDAOImpl categoryDAO;
    private final ProductDAOImpl productDAO;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
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

        Category savedCategory = categoryDAO.save(categoryMapper.toEntity(categoryDTO, products));
        logger.info("Category saved successfully: {}", savedCategory);
        return categoryMapper.toDTO(savedCategory);
    }


    @Override
    public CategoryDTO findCategoryById(Long id) {
        if (id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }
        return categoryDAO.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public CategoryDTO findCategoryByTitle(String title) {
        if (title == null) {
            logger.error("Invalid category title: {}", title);
            throw new BadRequestException("Invalid category title");
        }
        return categoryDAO.findByTitle(title)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
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
    public List<CategoryDTO> findAllCategories() {
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
    public void deleteCategory(Long id) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        categoryDAO.deleteById(id);
    }
}

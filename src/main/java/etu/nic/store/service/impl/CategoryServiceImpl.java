package etu.nic.store.service.impl;

import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.CategoryDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.mappers.CategoryMapper;
import etu.nic.store.repository.CategoryRepository;
import etu.nic.store.service.CategoryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            logger.error("CategoryDTO is null");
            throw new BadRequestException("CategoryDTO is null");
        }
        try {

            Category savedCategory = categoryRepository.
                    save(categoryMapper.toEntity(categoryDTO));
            logger.info("Category saved successfully {}", savedCategory);
            return categoryMapper.toDTO(savedCategory);
        }catch (BadRequestException e){
            logger.error("Failed to save product:{}", e.getMessage());
            throw new BadRequestException("Failed to save category");
        }
    }

    @Override
    public CategoryDTO findCategoryById(Long id) {
         if (id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }
         logger.info("Category found with id: {}", categoryRepository.findById(id));
        return categoryRepository.findById(id)
                .map(categoryMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public CategoryDTO findCategoryByTitle(String title) {
        if (title == null) {
            logger.error("Invalid category title: {}", title);
            throw new BadRequestException("Invalid category title");
        }
        logger.info("Category title: {}", categoryRepository.findByTitle(title));
        return categoryRepository.findByTitle(title)
                .map(categoryMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }


    @Override
    public List<CategoryDTO> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            logger.error("No categories found");
            throw new NotFoundException("No categories found");
        }
        return categories.stream()
                .map(categoryMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

    }


    // Хардовое удаление чтобы не повторять полностью продукт
    @Override
    public void deleteCategory(Long id) {
        if (id <= 0) {
            logger.error("Invalid category ID: {}", id);
            throw new BadRequestException("Invalid category ID");
        }
        categoryRepository.deleteById(id);
    }
}

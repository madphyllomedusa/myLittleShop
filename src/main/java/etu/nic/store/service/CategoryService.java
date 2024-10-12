package etu.nic.store.service;

import etu.nic.store.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategoryChildren(Long id);
    CategoryDto saveCategory(CategoryDto categoryDTO);
    CategoryDto findCategoryById(Long id);
    CategoryDto findCategoryByTitle(String title);
    CategoryDto updateCategory(Long id, CategoryDto categoryDTO);
    List<CategoryDto> findAllCategories();
    void deleteCategory(Long id);
}

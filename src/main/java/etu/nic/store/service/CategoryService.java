package etu.nic.store.service;

import etu.nic.store.model.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO saveCategory(CategoryDTO categoryDTO);
    CategoryDTO findCategoryById(Long id);
    CategoryDTO findCategoryByTitle(String title);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    List<CategoryDTO> findAllCategories();
    void deleteCategory(Long id);
}

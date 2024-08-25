package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.CategoryDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setDeletedTime(category.getDeletedTime());
        return dto;
    }

    public Category toEntity(CategoryDTO categoryDTO, Set<Product> products) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setParentId(categoryDTO.getParentId());
        category.setProducts(products);
        return category;
    }

}


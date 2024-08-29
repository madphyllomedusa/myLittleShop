package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.CategoryDto;
import etu.nic.store.model.pojo.Category;
import etu.nic.store.model.pojo.Product;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CategoryMapper {

    public CategoryDto toDTO(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setDeletedTime(category.getDeletedTime());
        return dto;
    }

    public Category toEntity(CategoryDto categoryDTO, Set<Product> products) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setParentId(categoryDTO.getParentId());
        category.setProducts(products);
        return category;
    }

}


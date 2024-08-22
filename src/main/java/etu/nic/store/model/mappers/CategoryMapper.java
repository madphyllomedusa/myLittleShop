package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.CategoryDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryDTO categoryDTO, Set<Product> products) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setProducts(products);
        return category;
    }

    public CategoryDTO toDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setTitle(category.getTitle());
        categoryDTO.setDescription(category.getDescription());
        Set<Long> productIds = category.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        categoryDTO.setProductIDs(productIds);
        return categoryDTO;
    }
}

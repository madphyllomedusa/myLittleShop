package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTO productDTO, Set<Category> categories) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setProductName(productDTO.getProductName());
        product.setProductDescription(productDTO.getProductDescription());
        product.setPrice(productDTO.getPrice());
        product.setDeletedTime(productDTO.getDeletedTime());
        product.setCategories(categories);
        return product;
    }

    public ProductDTO toDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setProductName(product.getProductName());
        productDTO.setProductDescription(product.getProductDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setDeletedTime(product.getDeletedTime());

        Set<Long> categoryIds = product.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        productDTO.setCategoryIds(categoryIds);

        return productDTO;
    }
}

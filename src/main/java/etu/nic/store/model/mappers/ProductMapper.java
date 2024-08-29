package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.ProductDto;
import etu.nic.store.model.pojo.Category;
import etu.nic.store.model.pojo.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {


    public ProductDto toDTO(Product product) {
        ProductDto productDTO = new ProductDto(){};
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategoryIds(product.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        productDTO.setParameters(product.getParameters());
        return productDTO;
    }

    public Product toEntity(ProductDto productDTO, Set<Category> categories) {
        Product product = new Product() {};
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategories(categories);
        product.setParameters(productDTO.getParameters());
        return product;
    }
}

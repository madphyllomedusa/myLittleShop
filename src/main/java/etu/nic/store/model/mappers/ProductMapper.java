package etu.nic.store.model.mappers;

import etu.nic.store.model.entity.Product;
import etu.nic.store.model.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
   ProductDTO toDto(Product product);
   Product toEntity(ProductDTO productDTO);
}

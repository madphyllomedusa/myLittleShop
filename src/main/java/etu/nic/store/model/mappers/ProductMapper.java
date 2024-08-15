package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDto(Product product);
    Product toEntity(ProductDTO productDTO);
}

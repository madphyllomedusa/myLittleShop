package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.CategoryDTO;
import etu.nic.store.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    Category toEntity(CategoryDTO categoryDTO);
    CategoryDTO toDTO(Category category);
}

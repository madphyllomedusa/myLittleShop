package etu.nic.store.model.mappers;

import etu.nic.store.dao.CategoryDao;
import etu.nic.store.model.dto.ProductDto;
import etu.nic.store.model.dto.SmartphoneDto;
import etu.nic.store.model.dto.WashingMachineDto;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.entity.Smartphone;
import etu.nic.store.model.entity.WashingMachine;
import etu.nic.store.model.enums.ProductType;
import etu.nic.store.exceptionhandler.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryDao categoryDAO;

    public ProductMapper(CategoryDao categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public ProductDto toDTO(Product product) {
        if (product instanceof Smartphone) {
            SmartphoneDto dto = new SmartphoneDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setDescription(product.getDescription());
            dto.setCategoryIds(product.getCategories()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
            dto.setType(ProductType.SMARTPHONE);
            dto.setModel(((Smartphone) product).getModel());
            dto.setColor(((Smartphone) product).getColor());
            dto.setStorageCapacity(((Smartphone) product).getStorageCapacity());
            return dto;
        } else if (product instanceof WashingMachine) {
            WashingMachineDto dto = new WashingMachineDto();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setDescription(product.getDescription());
            dto.setCategoryIds(product.getCategories()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
            dto.setType(ProductType.WASHING_MACHINE);
            dto.setSpinSpeed(((WashingMachine) product).getSpinSpeed());
            return dto;
        }
        throw new UnsupportedOperationException("Unsupported product type");
    }

    public Product toEntity(ProductDto productDTO) {
        Set<Category> categories = productDTO.getCategoryIds().stream()
                .map(categoryDAO::findById)
                .map(optionalCategory -> optionalCategory.orElseThrow(() -> new NotFoundException("Category not found")))
                .collect(Collectors.toSet());

        if (productDTO instanceof SmartphoneDto) {
            Smartphone smartphone = new Smartphone();
            smartphone.setId(productDTO.getId());
            smartphone.setName(productDTO.getName());
            smartphone.setPrice(productDTO.getPrice());
            smartphone.setDescription(productDTO.getDescription());
            smartphone.setCategories(categories);
            smartphone.setType(ProductType.SMARTPHONE);
            smartphone.setModel(((SmartphoneDto) productDTO).getModel());
            smartphone.setColor(((SmartphoneDto) productDTO).getColor());
            smartphone.setStorageCapacity(((SmartphoneDto) productDTO)
                    .getStorageCapacity());
            return smartphone;
        } else if (productDTO instanceof WashingMachineDto) {
            WashingMachine washingMachine = new WashingMachine();
            washingMachine.setId(productDTO.getId());
            washingMachine.setName(productDTO.getName());
            washingMachine.setPrice(productDTO.getPrice());
            washingMachine.setDescription(productDTO.getDescription());
            washingMachine.setCategories(categories);
            washingMachine.setType(ProductType.WASHING_MACHINE);
            washingMachine.setSpinSpeed(((WashingMachineDto) productDTO)
                    .getSpinSpeed());
            return washingMachine;
        }
        throw new UnsupportedOperationException("Unsupported product type");
    }
}

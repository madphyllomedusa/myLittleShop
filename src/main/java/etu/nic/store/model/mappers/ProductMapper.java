package etu.nic.store.model.mappers;

import etu.nic.store.dao.CategoryDAO;
import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.dto.SmartphoneDTO;
import etu.nic.store.model.dto.WashingMachineDTO;
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

    private final CategoryDAO categoryDAO;

    public ProductMapper(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public ProductDTO toDTO(Product product) {
        if (product instanceof Smartphone) {
            SmartphoneDTO dto = new SmartphoneDTO();
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
            WashingMachineDTO dto = new WashingMachineDTO();
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

    public Product toEntity(ProductDTO productDTO) {
        Set<Category> categories = productDTO.getCategoryIds().stream()
                .map(categoryDAO::findById)
                .map(optionalCategory -> optionalCategory.orElseThrow(() -> new NotFoundException("Category not found")))
                .collect(Collectors.toSet());

        if (productDTO instanceof SmartphoneDTO) {
            Smartphone smartphone = new Smartphone();
            smartphone.setId(productDTO.getId());
            smartphone.setName(productDTO.getName());
            smartphone.setPrice(productDTO.getPrice());
            smartphone.setDescription(productDTO.getDescription());
            smartphone.setCategories(categories);
            smartphone.setType(ProductType.SMARTPHONE);
            smartphone.setModel(((SmartphoneDTO) productDTO).getModel());
            smartphone.setColor(((SmartphoneDTO) productDTO).getColor());
            smartphone.setStorageCapacity(((SmartphoneDTO) productDTO)
                    .getStorageCapacity());
            return smartphone;
        } else if (productDTO instanceof WashingMachineDTO) {
            WashingMachine washingMachine = new WashingMachine();
            washingMachine.setId(productDTO.getId());
            washingMachine.setName(productDTO.getName());
            washingMachine.setPrice(productDTO.getPrice());
            washingMachine.setDescription(productDTO.getDescription());
            washingMachine.setCategories(categories);
            washingMachine.setType(ProductType.WASHING_MACHINE);
            washingMachine.setSpinSpeed(((WashingMachineDTO) productDTO)
                    .getSpinSpeed());
            return washingMachine;
        }
        throw new UnsupportedOperationException("Unsupported product type");
    }
}

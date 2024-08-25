package etu.nic.store.model.dto;

import etu.nic.store.model.entity.Product;
import etu.nic.store.model.enums.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmartphoneDTO extends ProductDTO{
    private String model;
    private String color;
    private String storageCapacity;

}

package etu.nic.store.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmartphoneDto extends ProductDto {
    private String model;
    private String color;
    private String storageCapacity;

}

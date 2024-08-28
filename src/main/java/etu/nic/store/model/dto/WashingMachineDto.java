package etu.nic.store.model.dto;

import etu.nic.store.model.enums.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WashingMachineDto extends ProductDto {
    private Integer spinSpeed;
    private ProductType Type;

}

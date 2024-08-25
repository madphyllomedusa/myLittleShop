package etu.nic.store.model.dto;

import etu.nic.store.model.enums.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class WashingMachineDTO extends ProductDTO {
    private Integer spinSpeed;
    private ProductType Type;

}

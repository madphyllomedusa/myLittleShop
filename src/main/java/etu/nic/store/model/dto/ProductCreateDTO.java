package etu.nic.store.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    private String productName;
    private String productDescription;
    private BigDecimal price;
}

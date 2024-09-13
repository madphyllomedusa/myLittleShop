package etu.nic.store.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private LocalDateTime deletedTime;
}
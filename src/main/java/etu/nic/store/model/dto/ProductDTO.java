package etu.nic.store.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProductDTO {
    private Long id;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private Set<Long> categoryIds;
    private LocalDateTime deletedTime;
}
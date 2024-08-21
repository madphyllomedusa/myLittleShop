package etu.nic.store.model.entity;

import lombok.Data;

import java.util.Set;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private Set<Category> categories;
    private LocalDateTime deletedTime;
}
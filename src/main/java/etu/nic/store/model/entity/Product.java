package etu.nic.store.model.entity;

import etu.nic.store.model.enums.ProductType;
import lombok.Data;

import java.util.Set;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public abstract class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Set<Category> categories;
    private LocalDateTime deletedTime;
    private ProductType type;
}
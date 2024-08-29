package etu.nic.store.model.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import java.math.BigDecimal;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Set<Category> categories;
    private OffsetDateTime deletedTime;
    private Map<String, String> parameters;
}

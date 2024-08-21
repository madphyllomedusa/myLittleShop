package etu.nic.store.model.entity;

import lombok.Data;

import java.util.Set;

@Data
public class Category {
    private Long id;
    private String title;
    private String description;
    private Set<Product> products;
}

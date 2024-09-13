package etu.nic.store.model.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class Category {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private OffsetDateTime deletedTime;
    private Set<Product> products;
}

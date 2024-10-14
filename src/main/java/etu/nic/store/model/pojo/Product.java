package etu.nic.store.model.pojo;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
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
    private List<String> imageUrls;
}

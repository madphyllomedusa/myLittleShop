package etu.nic.store.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

@Data

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Set<Long> categoryIds;
    private OffsetDateTime deletedTime;
    private Map<String, String> parameters;
}

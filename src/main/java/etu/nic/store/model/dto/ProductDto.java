package etu.nic.store.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import etu.nic.store.model.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SmartphoneDto.class, name = "SMARTPHONE"),
    @JsonSubTypes.Type(value = WashingMachineDto.class, name = "WASHING_MACHINE")
})
public abstract class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Set<Long> categoryIds;
    private LocalDateTime deletedTime;
    private ProductType type;
}
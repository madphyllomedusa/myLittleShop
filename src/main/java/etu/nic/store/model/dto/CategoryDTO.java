package etu.nic.store.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CategoryDTO {
    private Long id;
    private String title;
    private String description;
    private Set<Long> productIDs;
}

package etu.nic.store.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CategoryDto {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private LocalDateTime deletedTime;
    private Set<Long> productIDs;

}

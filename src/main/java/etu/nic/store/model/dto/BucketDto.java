
package etu.nic.store.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BucketDto {
    private Long id;
    private Long userId;
    private BigDecimal totalCost;
    private List<BucketItemDto> items;
}

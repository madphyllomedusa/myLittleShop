package etu.nic.store.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BucketItem {
    private Long id;
    private Long productId;
    private Long bucketId;
    private Integer quantity;
    private BigDecimal itemTotalCost;
}

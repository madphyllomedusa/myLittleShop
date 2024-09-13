package etu.nic.store.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bucket {
    private Long id;
    private Long userId;
    private BigDecimal totalCost;
    private List<BucketItem> items = new ArrayList<>();

   public void calculateTotalCost() {
        totalCost = items.stream()
            .map(BucketItem::getItemTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

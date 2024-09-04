package etu.nic.store.model.pojo;

import java.math.BigDecimal;
import java.util.List;

public class Bucket {
    private Long id;
    private User user;
    private List<Long> productId;
    private BigDecimal totalPrice;
    private Integer totalAmount;
}

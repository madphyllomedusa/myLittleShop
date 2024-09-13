package etu.nic.store.model.mappers;

import etu.nic.store.model.dto.BucketDto;
import etu.nic.store.model.dto.BucketItemDto;
import etu.nic.store.model.pojo.Bucket;
import etu.nic.store.model.pojo.BucketItem;
import etu.nic.store.model.pojo.Product;
import etu.nic.store.dao.ProductDao;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BucketMapper {

    private final ProductDao productDao;

    public BucketDto toDto(Bucket bucket) {
        List<BucketItemDto> itemDtos = bucket.getItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new BucketDto(
                bucket.getId(),
                bucket.getUserId(),
                bucket.getTotalCost(),
                itemDtos
        );
    }


    public BucketItemDto toDto(BucketItem item) {
        Product product = productDao.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return new BucketItemDto(
                item.getId(),
                item.getProductId(),
                product.getName(),
                item.getQuantity(),
                item.getItemTotalCost()
        );
    }

    public Bucket toEntity(BucketDto bucketDto) {
        List<BucketItem> items = bucketDto.getItems().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        return new Bucket(
                bucketDto.getId(),
                bucketDto.getUserId(),
                bucketDto.getTotalCost(),
                items
        );
    }

    public BucketItem toEntity(BucketItemDto itemDto) {
        return new BucketItem(
                itemDto.getId(),
                itemDto.getProductId(),
                null,  // bucketId будет присвоен позже
                itemDto.getQuantity(),
                itemDto.getItemTotalCost()
        );
    }

    public Bucket mapRowToBucket(ResultSet rs, int rowNum) throws SQLException {
        return new Bucket(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getBigDecimal("total_cost"),
                List.of()  // Элементы корзины добавляются отдельно
        );
    }

    public BucketItem mapRowToBucketItem(ResultSet rs, int rowNum) throws SQLException {
        return new BucketItem(
                rs.getLong("id"),
                rs.getLong("product_id"),
                rs.getLong("bucket_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("item_total_cost")
        );
    }
}

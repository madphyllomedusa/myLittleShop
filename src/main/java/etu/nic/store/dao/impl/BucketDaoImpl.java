package etu.nic.store.dao.impl;

import etu.nic.store.dao.BucketDao;
import etu.nic.store.model.mappers.BucketMapper;
import etu.nic.store.model.pojo.Bucket;
import etu.nic.store.model.pojo.BucketItem;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BucketDaoImpl implements BucketDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BucketMapper bucketMapper;

    @Override
    public Optional<Bucket> findByUserId(Long userId) {
        String sql = "SELECT * FROM buckets WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        List<Bucket> buckets = jdbcTemplate.query(sql, params, bucketMapper::mapRowToBucket);

        if (!buckets.isEmpty()) {
            Bucket bucket = buckets.get(0);
            bucket.setItems(getBucketItems(bucket.getId()));
            return Optional.of(bucket);
        }
        return Optional.empty();
    }

    @Override
    public Bucket save(Bucket bucket) {
        String sql = "INSERT INTO buckets (user_id, total_cost) VALUES (:userId, :totalCost)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", bucket.getUserId())
                .addValue("totalCost", bucket.getTotalCost());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        bucket.setId(keyHolder.getKey().longValue());
        return bucket;
    }

    @Override
    public void update(Bucket bucket) {
        String sql = "UPDATE buckets SET total_cost = :totalCost WHERE id = :bucketId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("totalCost", bucket.getTotalCost())
                .addValue("bucketId", bucket.getId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void addItemToBucket(BucketItem item) {
        String sql = "INSERT INTO bucket_items (product_id, bucket_id, quantity, item_total_cost) " +
                "VALUES (:productId, :bucketId, :quantity, :itemTotalCost)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", item.getProductId())
                .addValue("bucketId", item.getBucketId())
                .addValue("quantity", item.getQuantity())
                .addValue("itemTotalCost", item.getItemTotalCost());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        item.setId(keyHolder.getKey().longValue());
    }


    @Override
    public void updateItemInBucket(BucketItem item) {
        String sql = "UPDATE bucket_items SET quantity = :quantity, item_total_cost = :itemTotalCost WHERE id = :itemId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("quantity", item.getQuantity())
                .addValue("itemTotalCost", item.getItemTotalCost())
                .addValue("itemId", item.getId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteItemFromBucket(Long itemId) {
        String sql = "DELETE FROM bucket_items WHERE id = :itemId";
        MapSqlParameterSource params = new MapSqlParameterSource("itemId", itemId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void clearBucket(Long bucketId) {
        String sql = "DELETE FROM bucket_items WHERE bucket_id = :bucketId";
        MapSqlParameterSource params = new MapSqlParameterSource("bucketId", bucketId);
        jdbcTemplate.update(sql, params);
    }

private List<BucketItem> getBucketItems(Long bucketId) {
    String sql = "SELECT bi.*, p.name as product_name FROM bucket_items bi " +
                 "JOIN products p ON bi.product_id = p.id " +
                 "WHERE bi.bucket_id = :bucketId";

    MapSqlParameterSource params = new MapSqlParameterSource("bucketId", bucketId);

    return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
        BucketItem item = new BucketItem();
        item.setId(rs.getLong("id"));
        item.setProductId(rs.getLong("product_id"));

        item.setQuantity(rs.getInt("quantity"));
        item.setItemTotalCost(rs.getBigDecimal("item_total_cost"));
        return item;
    });
}
}

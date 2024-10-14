package etu.nic.store.dao.impl;

import etu.nic.store.dao.ProductDao;
import etu.nic.store.model.pojo.Category;
import etu.nic.store.model.pojo.Product;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProductDaoImpl implements ProductDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Product> findAllProducts() {
        String sql = "SELECT * FROM products WHERE deleted_time IS NULL";
        return namedParameterJdbcTemplate.query(sql, this::mapRowToProduct);
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = :id AND deleted_time IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.query(sql, params, this::mapRowToProduct)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Product> findDeletedProductById(Long id) {
        String sql = "SELECT * FROM products WHERE id = :id AND deleted_time IS NOT NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.query(sql, params, this::mapRowToProduct)
                .stream()
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (name, description, price, deleted_time) " +
                "VALUES (:name, :description, :price, :deletedTime) RETURNING id";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("description", product.getDescription());
        params.addValue("price", product.getPrice());
        params.addValue("deletedTime", product.getDeletedTime());

        Long id = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        product.setId(id);

        for (Map.Entry<String, String> entry : product.getParameters().entrySet()) {
            Long parameterId = getOrCreateParameterId(entry.getKey());
            String parameterValue = entry.getValue();

            String productParameterSql = "INSERT INTO product_parameter (product_id, parameter_id, parameter_value) " +
                    "VALUES (:productId, :parameterId, :parameterValue)";

            MapSqlParameterSource productParameterParams = new MapSqlParameterSource();
            productParameterParams.addValue("productId", product.getId());
            productParameterParams.addValue("parameterId", parameterId);
            productParameterParams.addValue("parameterValue", parameterValue);

            namedParameterJdbcTemplate.update(productParameterSql, productParameterParams);
        }

        saveProductImages(product);


        return product;
    }


    @Override
    public Product update(Product product) {
        String sql = "UPDATE products SET name = :name, description = :description, price = :price, " +
                " deleted_time = :deletedTime WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("description", product.getDescription());
        params.addValue("price", product.getPrice());
        params.addValue("deletedTime", product.getDeletedTime());
        params.addValue("id", product.getId());

        namedParameterJdbcTemplate.update(sql, params);

        String deleteSql = "DELETE FROM product_parameter WHERE product_id = :productId";
        MapSqlParameterSource deleteParams = new MapSqlParameterSource("productId", product.getId());
        namedParameterJdbcTemplate.update(deleteSql, deleteParams);

        for (Map.Entry<String, String> entry : product.getParameters().entrySet()) {
            Long parameterId = getOrCreateParameterId(entry.getKey());
            String parameterValue = entry.getValue();

            String productParameterSql = "INSERT INTO product_parameter (product_id, parameter_id, parameter_value) " +
                    "VALUES (:productId, :parameterId, :parameterValue)";

            MapSqlParameterSource productParameterParams = new MapSqlParameterSource();
            productParameterParams.addValue("productId", product.getId());
            productParameterParams.addValue("parameterId", parameterId);
            productParameterParams.addValue("parameterValue", parameterValue);

            namedParameterJdbcTemplate.update(productParameterSql, productParameterParams);
        }

        if (product.getImageUrls() != null) {
                addImageToProduct(product.getId(),product.getImageUrls());
        }
        return product;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "UPDATE products SET deleted_time = NOW() WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        String sql = "SELECT p.* " +
                "FROM products p " +
                "JOIN product_category pc ON p.id = pc.product_id " +
                "WHERE pc.category_id = :categoryId AND p.deleted_time IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("categoryId", categoryId);
        return namedParameterJdbcTemplate.query(sql, params, this::mapRowToProduct);
    }


    @Override
    public void addImageToProduct(Long productId, List<String> imageUrls) {
        String sql = "INSERT INTO product_images (product_id, image_url) VALUES (:productId, :imageUrl)";
        for (String imgUrl : imageUrls) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("productId", productId)
                    .addValue("imageUrl", imgUrl);
            namedParameterJdbcTemplate.update(sql, params);
        }
    }


    @Override
    public void addCategoryToProduct(Long productId, Long categoryId) {
        String sql = "INSERT INTO product_category (product_id, category_id) VALUES (:productId, :categoryId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);
        params.addValue("categoryId", categoryId);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void removeCategoryFromProduct(Long productId, Long categoryId) {
        String sql = "DELETE FROM product_category WHERE product_id = :productId AND category_id = :categoryId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);
        params.addValue("categoryId", categoryId);

        namedParameterJdbcTemplate.update(sql, params);
    }


    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product() {
        };
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        Timestamp deletedTimestamp = rs.getTimestamp("deleted_time");
        product.setDeletedTime(deletedTimestamp != null ? deletedTimestamp.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null);

        Set<Category> categories = getCategoriesByProductId(product.getId());
        product.setCategories(categories);

        Map<String, String> parameters = getProductParametersByProductId(product.getId());
        product.setParameters(parameters);

        List<String> imageUrls = getProductImages(product.getId());
        product.setImageUrls(imageUrls);

        return product;
    }

    private void saveProductImages(Product product) {
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            String sql = "INSERT INTO product_images (product_id, image_url) VALUES (:productId, :imageUrl)";
            for (String imageUrl : product.getImageUrls()) {
                MapSqlParameterSource params = new MapSqlParameterSource()
                        .addValue("productId", product.getId())
                        .addValue("imageUrl", imageUrl);
                namedParameterJdbcTemplate.update(sql, params);
            }
        }
    }

    private List<String> getProductImages(Long productId) {
        String sql = "SELECT image_url FROM product_images WHERE product_id = :productId";
        MapSqlParameterSource params = new MapSqlParameterSource("productId", productId);
        return namedParameterJdbcTemplate.queryForList(sql, params, String.class);
    }

    private Long getOrCreateParameterId(String parameterName) {
        String selectSql = "SELECT id FROM parameters WHERE name = :name";
        MapSqlParameterSource selectParams = new MapSqlParameterSource("name", parameterName);

        List<Long> ids = namedParameterJdbcTemplate.queryForList(selectSql, selectParams, Long.class);

        if (!ids.isEmpty()) {
            return ids.get(0);
        } else {
            String insertSql = "INSERT INTO parameters (name) VALUES (:name) RETURNING id";
            return namedParameterJdbcTemplate.queryForObject(insertSql, selectParams, Long.class);
        }
    }

    private Set<Category> getCategoriesByProductId(Long productId) {
        String sql = "SELECT c.* FROM categories c INNER JOIN product_category pc ON c.id = pc.category_id WHERE pc.product_id = :productId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setTitle(rs.getString("title"));
            category.setDescription(rs.getString("description"));
            return category;
        }).stream()
                .collect(Collectors.toSet());
    }


    private Map<String, String> getProductParametersByProductId(Long productId) {
        String sql = "SELECT p.name, pp.parameter_value FROM parameters p INNER JOIN product_parameter pp ON p.id = pp.parameter_id WHERE pp.product_id = :productId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            return new AbstractMap.SimpleEntry<>(rs.getString("name"), rs.getString("parameter_value"));
        }).stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

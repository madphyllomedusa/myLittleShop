package etu.nic.store.dao.impl;

import etu.nic.store.dao.ProductDao;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.entity.Smartphone;
import etu.nic.store.model.entity.WashingMachine;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        String sql = "INSERT INTO products (name, description, price, type, deleted_time) " +
                "VALUES (:name, :description, :price, :type, :deletedTime) RETURNING id";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("description", product.getDescription());
        params.addValue("price", product.getPrice());
        params.addValue("type", product.getType().toString());
        params.addValue("deletedTime", product.getDeletedTime());

        Long id = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        product.setId(id);

        if (product instanceof Smartphone) {
            saveSmartphoneDetails((Smartphone) product);
        } else if (product instanceof WashingMachine) {
            saveWashingMachineDetails((WashingMachine) product);
        }

        return product;
    }


    @Override
    public Product update(Product product) {
        String sql = "UPDATE products SET name = :name, description = :description, price = :price, " +
                "type = :type, deleted_time = :deletedTime WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("description", product.getDescription());
        params.addValue("price", product.getPrice());
        params.addValue("type", product.getType().toString());
        params.addValue("deletedTime", product.getDeletedTime());
        params.addValue("id", product.getId());

        namedParameterJdbcTemplate.update(sql, params);

        if (product instanceof Smartphone) {
            updateSmartphoneDetails((Smartphone) product);
        } else if (product instanceof WashingMachine) {
            updateWashingMachineDetails((WashingMachine) product);
        }

        updateProductCategories(product.getId(), product.getCategories());
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
        String productType = rs.getString("type");

        Product product;
        if ("SMARTPHONE".equals(productType)) {
            product = mapRowToSmartphone(rs);
        } else if ("WASHING_MACHINE".equals(productType)) {
            product = mapRowToWashingMachine(rs);
        } else {
            product = new Product() {
            };
        }
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        Timestamp deletedTimestamp = rs.getTimestamp("deleted_time");
        product.setDeletedTime(deletedTimestamp != null ? deletedTimestamp.toLocalDateTime() : null);

        Set<Category> categories = getCategoriesByProductId(product.getId());
        product.setCategories(categories);

        return product;
    }

    private Smartphone mapRowToSmartphone(ResultSet rs) throws SQLException {
        String sql = "SELECT * FROM smartphones WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", rs.getLong("id"));

        return namedParameterJdbcTemplate.queryForObject(sql, params, (smartphoneRs, rowNum) -> {
            Smartphone smartphone = new Smartphone();
            smartphone.setId(rs.getLong("id"));
            smartphone.setName(rs.getString("name"));
            smartphone.setPrice(rs.getBigDecimal("price"));
            smartphone.setDescription(rs.getString("description"));
            smartphone.setColor(smartphoneRs.getString("color"));
            smartphone.setStorageCapacity(smartphoneRs.getString("storage_capacity"));
            return smartphone;
        });
    }

    private WashingMachine mapRowToWashingMachine(ResultSet rs) throws SQLException {
        String sql = "SELECT * FROM washing_machines WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", rs.getLong("id"));

        return namedParameterJdbcTemplate.queryForObject(sql, params, (washingMachineRs, rowNum) -> {
            WashingMachine washingMachine = new WashingMachine();
            washingMachine.setId(rs.getLong("id"));
            washingMachine.setName(rs.getString("name"));
            washingMachine.setPrice(rs.getBigDecimal("price"));
            washingMachine.setDescription(rs.getString("description"));
            washingMachine.setSpinSpeed(washingMachineRs.getInt("spin_speed"));
            return washingMachine;
        });
    }

    private void saveSmartphoneDetails(Smartphone smartphone) {
        String sql = "INSERT INTO smartphones (id, color, storage_capacity, model) VALUES (:id, :color, :storageCapacity, :model)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", smartphone.getId());
        params.addValue("color", smartphone.getColor());
        params.addValue("storageCapacity", smartphone.getStorageCapacity());
        params.addValue("model", smartphone.getModel());

        namedParameterJdbcTemplate.update(sql, params);
    }

    private void saveWashingMachineDetails(WashingMachine washingMachine) {
        String sql = "INSERT INTO washing_machines (id, spin_speed) VALUES (:id, :spinSpeed)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", washingMachine.getId());
        params.addValue("spinSpeed", washingMachine.getSpinSpeed());

        namedParameterJdbcTemplate.update(sql, params);
    }


    private void updateSmartphoneDetails(Smartphone smartphone) {
        String sql = "UPDATE smartphones SET color = :color, storage_capacity = :storageCapacity, model = :model WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("color", smartphone.getColor());
        params.addValue("storageCapacity", smartphone.getStorageCapacity());
        params.addValue("model", smartphone.getModel());
        params.addValue("id", smartphone.getId());

        namedParameterJdbcTemplate.update(sql, params);
    }

    private void updateWashingMachineDetails(WashingMachine washingMachine) {
        String sql = "UPDATE washing_machines SET spin_speed = :spinSpeed WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("spinSpeed", washingMachine.getSpinSpeed());
        params.addValue("id", washingMachine.getId());

        namedParameterJdbcTemplate.update(sql, params);
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
        }).stream().collect(Collectors.toSet());
    }


    private void saveProductCategories(Long productId, Set<Category> categories) {
        String sql = "INSERT INTO product_category (product_id, category_id) VALUES (:productId, :categoryId)";

        for (Category category : categories) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("productId", productId);
            params.addValue("categoryId", category.getId());

            namedParameterJdbcTemplate.update(sql, params);
        }
    }

    private void updateProductCategories(Long productId, Set<Category> categories) {
        String deleteSql = "DELETE FROM product_category WHERE product_id = :productId";

        MapSqlParameterSource deleteParams = new MapSqlParameterSource();
        deleteParams.addValue("productId", productId);

        namedParameterJdbcTemplate.update(deleteSql, deleteParams);

        saveProductCategories(productId, categories);
    }
}

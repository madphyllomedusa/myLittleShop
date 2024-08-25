package etu.nic.store.dao.impl;

import etu.nic.store.dao.ProductDAO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.entity.Smartphone;
import etu.nic.store.model.entity.WashingMachine;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ProductDAOImpl implements ProductDAO {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Product> findAllProducts() {
        String sql = "SELECT * FROM products WHERE deleted_time IS NULL";
        return jdbcTemplate.query(sql, this::mapRowToProduct);
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ? AND deleted_time IS NULL";
        return jdbcTemplate.query(sql, this::mapRowToProduct, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Product> findProductByIdAndDeleted(Long id) {
        String sql = "SELECT * FROM products WHERE id = ? AND deleted_time IS NOT NULL";
        return jdbcTemplate.query(sql, this::mapRowToProduct, id)
                .stream()
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (product_name," +
                " product_description," +
                " price," +
                " product_type," +
                " deleted_time) VALUES (?, ?, ?, ?, ?) RETURNING id";

        if (product.getType() == null) {
            throw new IllegalArgumentException("Product type cannot be null");
        }

        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getType().toString(),
                product.getDeletedTime());
        product.setId(id);

        if (product instanceof Smartphone) {
            saveSmartphoneDetails((Smartphone) product);
        } else if (product instanceof WashingMachine) {
            saveWashingMachineDetails((WashingMachine) product);
        }

        for (Category category : product.getCategories()) {
            addCategoryToProduct(product.getId(), category.getId());
        }

        return product;
    }


    @Override
    public Product update(Product product) {
        String sql = "UPDATE products SET product_name = ?," +
                " product_description = ?," +
                " price = ?," +
                " product_type = ?," +
                " deleted_time = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getType(),
                product.getDeletedTime(),
                product.getId());

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
        String sql = "UPDATE products SET deleted_time = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addCategoryToProduct(Long productId, Long categoryId) {
        String sql = "INSERT INTO product_category (product_id, category_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, productId, categoryId);
    }

    @Override
    public void removeCategoryFromProduct(Long productId, Long categoryId) {
        String sql = "DELETE FROM product_category WHERE product_id = ? AND category_id = ?";
        jdbcTemplate.update(sql, productId, categoryId);
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        String productType = rs.getString("product_type");

        Product product;
        if ("SMARTPHONE".equals(productType)) {
            product = mapRowToSmartphone(rs);
        } else if ("WASHING_MACHINE".equals(productType)) {
            product = mapRowToWashingMachine(rs);
        } else {
            product = new Product() {};
        }
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("product_name"));
        product.setDescription(rs.getString("product_description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setDeletedTime(rs.getTimestamp("deleted_time") != null ? rs.getTimestamp("deleted_time").toLocalDateTime() : null);

        Set<Category> categories = getCategoriesByProductId(product.getId());
        product.setCategories(categories);

        return product;
    }

    private Smartphone mapRowToSmartphone(ResultSet rs) throws SQLException {
        String sql = "SELECT * FROM smartphones WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (smartphoneRs, rowNum) -> {
            Smartphone smartphone = new Smartphone();
            smartphone.setId(rs.getLong("id"));
            smartphone.setName(rs.getString("product_name"));
            smartphone.setPrice(rs.getBigDecimal("price"));
            smartphone.setDescription(rs.getString("product_description"));
            smartphone.setColor(smartphoneRs.getString("color"));
            smartphone.setStorageCapacity(smartphoneRs.getString("storage_capacity"));
            return smartphone;
        }, rs.getLong("id"));
    }

    private WashingMachine mapRowToWashingMachine(ResultSet rs) throws SQLException {
        String sql = "SELECT * FROM washing_machines WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (washingMachineRs, rowNum) -> {
            WashingMachine washingMachine = new WashingMachine();
            washingMachine.setId(rs.getLong("id"));
            washingMachine.setName(rs.getString("product_name"));
            washingMachine.setPrice(rs.getBigDecimal("price"));
            washingMachine.setDescription(rs.getString("product_description"));
            washingMachine.setSpinSpeed(washingMachineRs.getInt("spin_speed"));
            return washingMachine;
        }, rs.getLong("id"));
    }

private void saveSmartphoneDetails(Smartphone smartphone) {
    String sql = "INSERT INTO smartphones (id, color, storage_capacity, model)" +
            " VALUES (?, ?, ?, ?)";
    jdbcTemplate.update(sql,
            smartphone.getId(),
            smartphone.getColor(),
            smartphone.getStorageCapacity(),
            smartphone.getModel());
}

    private void saveWashingMachineDetails(WashingMachine washingMachine) {
        String sql = "INSERT INTO washing_machines (id, spin_speed)" +
                " VALUES (?, ?)";
        jdbcTemplate.update(sql,
                washingMachine.getId(),
                washingMachine.getSpinSpeed());
    }

private void updateSmartphoneDetails(Smartphone smartphone) {
    String sql = "UPDATE smartphones SET color = ?, storage_capacity = ?, model = ?" +
            " WHERE id = ?";
    jdbcTemplate.update(sql,
            smartphone.getColor(),
            smartphone.getStorageCapacity(),
            smartphone.getModel(),  // Обновление нового поля
            smartphone.getId());
}

    private void updateWashingMachineDetails(WashingMachine washingMachine) {
        String sql = "UPDATE washing_machines SET  spin_speed = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sql,
                washingMachine.getSpinSpeed(),
                washingMachine.getId());
    }

    private Set<Category> getCategoriesByProductId(Long productId) {
        String sql = "SELECT c.* FROM categories c INNER JOIN product_category pc ON c.id = pc.category_id" +
                " WHERE pc.product_id = ?";
        return jdbcTemplate.query(sql, new Object[]{productId}, (rs, rowNum) -> {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setTitle(rs.getString("title"));
            category.setDescription(rs.getString("description"));
            return category;
        })
                .stream()
                .collect(Collectors.toSet());
    }

    private void saveProductCategories(Long productId, Set<Category> categories) {
        String sql = "INSERT INTO product_category (product_id, category_id) " +
                "VALUES (?, ?)";
        for (Category category : categories) {
            jdbcTemplate.update(sql, productId, category.getId());
        }
    }

    private void updateProductCategories(Long productId, Set<Category> categories) {
        String deleteSql = "DELETE FROM product_category" +
                " WHERE product_id = ?";
        jdbcTemplate.update(deleteSql, productId);

        saveProductCategories(productId, categories);
    }
}

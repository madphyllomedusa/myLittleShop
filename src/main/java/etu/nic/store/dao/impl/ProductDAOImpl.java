package etu.nic.store.dao.impl;

import etu.nic.store.dao.ProductDAO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    public Optional<Product> findProductById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ? AND deleted_time IS NULL";
        List<Product> products = jdbcTemplate.query(sql, this::mapRowToProduct, id);
        return products.stream().findFirst();
    }

    @Override
    public Optional<Product> findProductByIdAndDeleted(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        List<Product> products = jdbcTemplate.query(sql, this::mapRowToProduct, id);
        return products.stream().findFirst();
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO products (product_name, product_description, price, deleted_time) VALUES (?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, product.getProductName(), product.getProductDescription(), product.getPrice(), product.getDeletedTime());
        product.setId(id);

        for (Category category : product.getCategories()) {
            addCategoryToProduct(product.getId(), category.getId());
        }

        return product;
    }

    @Override
    public void addCategoryToProduct(Long productId, Long categoryId) {
        String sql = "INSERT INTO product_category (product_id, category_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, productId, categoryId);
    }


    @Override
    public Product update(Product product) {
        String sql = "UPDATE products SET product_name = ?, product_description = ?, price = ?, deleted_time = ? WHERE id = ?";
        jdbcTemplate.update(sql, product.getProductName(), product.getProductDescription(), product.getPrice(), product.getDeletedTime(), product.getId());
        return product;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "UPDATE products SET deleted_time = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


    @Override
    public void removeCategoryFromProduct(Long productId, Long categoryId) {
        String sql = "DELETE FROM product_category WHERE product_id = ? AND category_id = ?";
        jdbcTemplate.update(sql, productId, categoryId);
    }


    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setProductName(rs.getString("product_name"));
        product.setProductDescription(rs.getString("product_description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setDeletedTime(rs.getTimestamp("deleted_time") != null ? rs.getTimestamp("deleted_time").toLocalDateTime() : null);
        return product;
    }
}

package etu.nic.store.dao.impl;

import etu.nic.store.dao.CategoryDAO;
import etu.nic.store.model.entity.Category;
import etu.nic.store.model.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CategoryDAOImpl implements CategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories WHERE deleted_time IS NULL";
        return jdbcTemplate.query(sql, this::mapRowToCategory);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ? AND deleted_time IS NULL";
        List<Category> categories = jdbcTemplate.query(sql, this::mapRowToCategory, id);
        return categories.stream().findFirst();
    }

    @Override
    public Optional<Category> findByTitle(String title) {
        String sql = "SELECT * FROM categories WHERE title = ?";
        List<Category> categories = jdbcTemplate.query(sql, this::mapRowToCategory, title);
        return categories.stream().findFirst();
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (title, description, parent_id) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql,
                Long.class,
                category.getTitle(),
                category.getDescription(),
                category.getParentId());
        category.setId(id);

        for (Product product : category.getProducts()) {
            addProductToCategory(category.getId(), product.getId());
        }
        return category;
    }


    @Override
    public void addProductToCategory(Long categoryId, Long productId) {
        String sql = "INSERT INTO product_category (category_id, product_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, categoryId, productId);
    }


    @Override
    public Category update(Category category) {
        String sql = "UPDATE categories SET title = ?, description = ?, parent_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                category.getTitle(),
                category.getDescription(),
                category.getParentId(),
                category.getId());
        return category;
    }

    @Override
    public void removeProductsFromCategory(Long categoryId) {
        String sql = "DELETE FROM product_category WHERE category_id = ?";
        jdbcTemplate.update(sql, categoryId);
    }


    @Override
    public void deleteById(Long id) {
        if (hasProducts(id)) {
            throw new IllegalStateException("Cannot delete category with products");
        }

        String sql = "UPDATE categories SET deleted_time = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private boolean hasProducts(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM product_category WHERE category_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, categoryId);
        return count != null && count > 0;
    }


    private Category mapRowToCategory(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setTitle(rs.getString("title"));
        category.setDescription(rs.getString("description"));
        category.setParentId(rs.getLong("parent_id"));
        category.setDeletedTime(rs.getTimestamp("deleted_time") != null ? rs.getTimestamp("deleted_time").toLocalDateTime() : null);
        return category;
    }
}

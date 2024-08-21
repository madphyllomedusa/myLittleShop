package etu.nic.store.dao.impl;

import etu.nic.store.dao.CategoryDAO;
import etu.nic.store.model.entity.Category;
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
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, this::mapRowToCategory);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
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
        String sql = "INSERT INTO categories (title, description) VALUES (?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, category.getTitle(), category.getDescription());
        category.setId(id);
        return category;
    }

    @Override
    public Category update(Category category) {
        String sql = "UPDATE categories SET title = ? description = ? WHERE id = ?";
        jdbcTemplate.update(sql, category.getTitle(), category.getDescription(),category.getId());
        return category;

    }

    @Override
    public void deleteById(Long id) {

    }

    private Category mapRowToCategory(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setTitle(rs.getString("title"));
        category.setDescription(rs.getString("description"));
        return category;
    }
}

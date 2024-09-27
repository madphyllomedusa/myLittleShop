package etu.nic.store.dao.impl;

import etu.nic.store.dao.CategoryDao;
import etu.nic.store.model.pojo.Category;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CategoryDaoImpl implements CategoryDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories WHERE deleted_time IS NULL";
        //todo добавить фильтр значений(допустим первые 10)
        return namedParameterJdbcTemplate.query(sql, this::mapRowToCategory);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = :id AND deleted_time IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Category> categories = namedParameterJdbcTemplate.query(sql, params, this::mapRowToCategory);
        return categories.stream().findFirst();
    }

    @Override
    public Optional<Category> findByTitle(String title) {
        String sql = "SELECT * FROM categories WHERE title = ?";
        MapSqlParameterSource params =  new MapSqlParameterSource();
        params.addValue("title",title);
        List<Category> categories = namedParameterJdbcTemplate.query(sql, params, this::mapRowToCategory);
        return categories.stream().findFirst();
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO categories (title, description, parent_id) VALUES (:title, :description, :parentId) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", category.getTitle());
        params.addValue("description", category.getDescription());
        params.addValue("parentId", category.getParentId());

        Long id = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        category.setId(id);

        return category;
    }


    @Override
    public void addProductToCategory(Long categoryId, Long productId) {
        String sql = "INSERT INTO product_category (category_id, product_id) VALUES (:categoryId, :productId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("categoryId", categoryId);
        params.addValue("productId", productId);

        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public Category update(Category category) {
        String sql = "UPDATE categories SET title = :title, description = :description, parent_id = :parentId WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", category.getTitle());
        params.addValue("description", category.getDescription());
        params.addValue("parentId", category.getParentId());
        params.addValue("id", category.getId());

        namedParameterJdbcTemplate.update(sql, params);
        return category;
    }

    @Override
    public void removeProductsFromCategory(Long categoryId) {
        String sql = "DELETE FROM product_category WHERE category_id = :categoryId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("categoryId", categoryId);

        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public void deleteById(Long id) {
        if (hasProducts(id)) {
            throw new IllegalStateException("Cannot delete category with products");
        }

        String sql = "UPDATE categories SET deleted_time = NOW() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        namedParameterJdbcTemplate.update(sql, params);
    }

    private boolean hasProducts(Long categoryId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM product_category WHERE category_id = :categoryId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("categoryId", categoryId);

        return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }



    private Category mapRowToCategory(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setTitle(rs.getString("title"));
        category.setDescription(rs.getString("description"));
        category.setParentId(rs.getLong("parent_id"));

        Timestamp deletedTimestamp = rs.getTimestamp("deleted_time");
        category.setDeletedTime(deletedTimestamp != null ? deletedTimestamp.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null);

        return category;
    }

}

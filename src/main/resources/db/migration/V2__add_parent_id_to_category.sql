ALTER TABLE categories
    ADD COLUMN parent_id BIGINT,
    ADD CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE;

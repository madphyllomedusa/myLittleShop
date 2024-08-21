CREATE TABLE IF NOT EXISTS categories(
                                         id BIGSERIAL PRIMARY KEY,
                                         title VARCHAR(255) NOT NULL,
                                         description TEXT
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        product_name VARCHAR(255) NOT NULL,
                                        product_description TEXT,
                                        price DECIMAL(10, 2) NOT NULL,
                                        deleted_time TIMESTAMP,
                                        CONSTRAINT check_price_positive CHECK (price > 0)
);

CREATE TABLE IF NOT EXISTS product_category (
                                                product_id BIGINT NOT NULL,
                                                category_id BIGINT NOT NULL,
                                                PRIMARY KEY (product_id, category_id),
                                                CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                                CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);


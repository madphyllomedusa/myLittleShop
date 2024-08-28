CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL UNIQUE ,
    description TEXT,
    parent_id BIGINT,
    deleted_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    type TEXT NOT NULL,
    deleted_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT check_price_positive CHECK (price > 0)
);

CREATE TABLE IF NOT EXISTS smartphones (
    id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    model TEXT,
    color TEXT,
    storage_capacity TEXT
);

CREATE TABLE IF NOT EXISTS washing_machines (
    id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    spin_speed INT
);

CREATE TABLE IF NOT EXISTS product_category (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

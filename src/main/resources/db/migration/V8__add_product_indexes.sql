CREATE INDEX idx_product_category_id ON product(category_id);

CREATE INDEX idx_product_is_active ON product(is_active);

CREATE INDEX idx_product_active_quantity ON product(is_active, quantity);

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_product_name_trgm ON product USING gin (name gin_trgm_ops);

CREATE UNIQUE INDEX idx_category_name ON category(name);
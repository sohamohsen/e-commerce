CREATE INDEX idx_product_category_id
    ON product(category_id);

CREATE INDEX idx_product_is_active
    ON product(is_active);

CREATE INDEX idx_product_active_quantity
    ON product(is_active, quantity);

CREATE FULLTEXT INDEX idx_product_name_ft
    ON product(name);

CREATE UNIQUE INDEX idx_category_name
    ON category(name);


CREATE TABLE product (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         category_id INT NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL,
                         quantity INT NOT NULL DEFAULT 0,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         image_url VARCHAR(500),

                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_product_category
                             FOREIGN KEY (category_id)
                                 REFERENCES category(id),
                         created_by INT NOT NULL,
                         updated_by INT
);
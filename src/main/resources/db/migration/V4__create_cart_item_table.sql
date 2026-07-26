CREATE TABLE cart_item (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT NOT NULL,
                           product_id INT NOT NULL,
                           quantity INT NOT NULL DEFAULT 1,

                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

                           CONSTRAINT fk_cart_item_product
                               FOREIGN KEY (product_id)
                                   REFERENCES product(id),

                           CONSTRAINT fk_cart_item_user
                               FOREIGN KEY (user_id)
                                   REFERENCES user(id)
);
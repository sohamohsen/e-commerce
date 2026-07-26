CREATE TABLE `orders` (
                         id INT AUTO_INCREMENT PRIMARY KEY,

                         user_id INT NOT NULL,

                         status VARCHAR(30) NOT NULL,
                         total_amount DECIMAL(10,2) NOT NULL,

                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_order_user
                             FOREIGN KEY (user_id)
                                 REFERENCES user(id)
);
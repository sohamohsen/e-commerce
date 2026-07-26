CREATE TABLE payment (
                         id INT AUTO_INCREMENT PRIMARY KEY,

                         order_id INT NOT NULL,

                         paymob_order_id VARCHAR(255),
                         transaction_id VARCHAR(255),
                         client_secret VARCHAR(500),

                         amount DECIMAL(10,2),
                         currency VARCHAR(10),

                         status VARCHAR(30) NOT NULL,
                         paid_at DATETIME,

                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_payment_order
                             FOREIGN KEY (order_id)
                                 REFERENCES `orders`(id)
);
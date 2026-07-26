CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    order_id INT NOT NULL,
    transaction_id VARCHAR(255),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    total_amount_cents BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    issued_at DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    html_snapshot TEXT,
    email_sent_at DATETIME,
    CONSTRAINT fk_bill_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

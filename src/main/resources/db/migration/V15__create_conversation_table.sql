CREATE TABLE conversation (

                              id BIGINT AUTO_INCREMENT PRIMARY KEY,

                              customer_id INT NOT NULL,

                              admin_id INT NULL,

                              status VARCHAR(20) NOT NULL,

                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP

);
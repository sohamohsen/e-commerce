CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,

                      name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      phone VARCHAR(20),
                      password VARCHAR(255) NOT NULL,

                      role VARCHAR(50) NOT NULL,

                      enabled BOOLEAN DEFAULT TRUE,
                      failed_login_attempt INT DEFAULT 0,
                      locked_until DATETIME NULL,
                      account_locked BOOLEAN DEFAULT FALSE,

                      created_by INT,
                      updated_by INT,

                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                          ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              title VARCHAR(200) NOT NULL,
                              message VARCHAR(1000) NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              is_read BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME,

                              CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE INDEX idx_notification_user_id ON notification(user_id);

CREATE INDEX idx_notification_user_unread ON notification(user_id, is_read);
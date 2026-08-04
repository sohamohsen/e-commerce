CREATE TABLE chat_message (

                              id BIGINT AUTO_INCREMENT PRIMARY KEY,

                              conversation_id BIGINT NOT NULL,

                              sender_id BIGINT NOT NULL,

                              content VARCHAR(2000) NOT NULL,

                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              INDEX idx_conversation(conversation_id)
);
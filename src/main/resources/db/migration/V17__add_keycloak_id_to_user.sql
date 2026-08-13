ALTER TABLE user
    ADD COLUMN keycloak_id VARCHAR(255) NULL;

CREATE UNIQUE INDEX uk_user_keycloak_id
    ON user(keycloak_id);
CREATE TABLE users (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    name                VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    address             VARCHAR(255) NOT NULL,
    user_type_id        BIGINT NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_user_type FOREIGN KEY (user_type_id) REFERENCES user_types(id)
);

CREATE TABLE user_types (
    id                          BIGINT NOT NULL AUTO_INCREMENT,
    name                        VARCHAR(50) NOT NULL,
    is_system                   BOOLEAN NOT NULL DEFAULT FALSE,
    can_manage_restaurants      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_types PRIMARY KEY (id),
    CONSTRAINT uk_user_types_name UNIQUE (name)
);

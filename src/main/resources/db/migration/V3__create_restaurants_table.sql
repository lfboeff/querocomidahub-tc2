CREATE TABLE restaurants (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    name             VARCHAR(255) NOT NULL,
    address          VARCHAR(500) NOT NULL,
    kitchen_type     VARCHAR(80) NOT NULL,
    opening_hours    VARCHAR(200) NOT NULL,
    owner_id         BIGINT NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_restaurants PRIMARY KEY (id),
    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

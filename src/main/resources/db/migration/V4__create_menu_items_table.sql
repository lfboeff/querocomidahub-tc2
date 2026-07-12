CREATE TABLE menu_items (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    restaurant_id    BIGINT NOT NULL,
    name             VARCHAR(255) NOT NULL,
    description      VARCHAR(500) NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    dine_in_only     BOOLEAN NOT NULL DEFAULT FALSE,
    photo_path       VARCHAR(500) NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_menu_items PRIMARY KEY (id),
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

package br.com.fiap.querocomidahub.user.domain.model;

import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;

public final class RestaurantOwnerUser extends UserBase {

    private RestaurantOwnerUser(String name, String email, String address, UserType userType) {
        super(name, email, address, userType);
    }

    private RestaurantOwnerUser(Long id, String name, String email, String address, UserType userType,
                                LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        super(id, name, email, address, userType, createdAt, lastModifiedAt);
    }

    static RestaurantOwnerUser create(String name, String email, String address, UserType userType) {
        return new RestaurantOwnerUser(name, email, address, userType);
    }

    static RestaurantOwnerUser reconstitute(Long id, String name, String email, String address,
                                            UserType userType, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        return new RestaurantOwnerUser(id, name, email, address, userType, createdAt, lastModifiedAt);
    }

    @Override
    public RestaurantOwnerUser withUpdatedParams(String name, String email, String address) {
        return new RestaurantOwnerUser(this.id, sanitizeName(name), sanitizeEmail(email), sanitizeAddress(address),
                this.userType, this.createdAt, this.lastModifiedAt);
    }
}

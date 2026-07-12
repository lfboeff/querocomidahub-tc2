package br.com.fiap.querocomidahub.user.domain.model;

import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
    }

    public static UserBase create(String name, String email, String address, UserType userType) {
        if (userType.canManageRestaurants()) {
            return RestaurantOwnerUser.create(name, email, address, userType);
        }
        return ClientUser.create(name, email, address, userType);
    }

    public static UserBase reconstitute(Long id, String name, String email, String address,
                                        UserType userType, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        if (userType.canManageRestaurants()) {
            return RestaurantOwnerUser.reconstitute(id, name, email, address, userType, createdAt, lastModifiedAt);
        }
        return ClientUser.reconstitute(id, name, email, address, userType, createdAt, lastModifiedAt);
    }
}

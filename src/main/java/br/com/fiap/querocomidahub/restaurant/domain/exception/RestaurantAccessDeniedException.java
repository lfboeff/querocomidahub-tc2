package br.com.fiap.querocomidahub.restaurant.domain.exception;

public class RestaurantAccessDeniedException extends RuntimeException {

    public RestaurantAccessDeniedException(Long userId, Long restaurantId) {
        super("User with id='" + userId + "' is not the owner of restaurant with id='" + restaurantId + "'");
    }
}

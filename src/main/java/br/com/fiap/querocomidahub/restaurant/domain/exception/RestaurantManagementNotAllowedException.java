package br.com.fiap.querocomidahub.restaurant.domain.exception;

public class RestaurantManagementNotAllowedException extends RuntimeException {
    public RestaurantManagementNotAllowedException(Long userId) {
        super("User with id='" + userId + "' does not have permission to manage restaurants");
    }
}

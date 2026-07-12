package br.com.fiap.querocomidahub.restaurant.domain.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("Restaurant with id='" + id + "' was not found");
    }
}

package br.com.fiap.querocomidahub.user.domain.exception;

public class UserInUseInRestaurantsException extends RuntimeException {
    public UserInUseInRestaurantsException(Long id) {
        super("User with id='" + id + "' cannot be deleted because it is assigned to one or more restaurants");
    }
}

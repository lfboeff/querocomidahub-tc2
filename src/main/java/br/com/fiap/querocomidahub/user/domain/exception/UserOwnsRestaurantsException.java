package br.com.fiap.querocomidahub.user.domain.exception;

public class UserOwnsRestaurantsException extends RuntimeException {
    public UserOwnsRestaurantsException(Long id) {
        super("User with id='" + id + "' cannot lose the ability to manage restaurants while it still owns one or more");
    }
}

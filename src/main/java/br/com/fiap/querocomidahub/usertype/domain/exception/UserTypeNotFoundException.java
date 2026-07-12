package br.com.fiap.querocomidahub.usertype.domain.exception;

public class UserTypeNotFoundException extends RuntimeException {

    public UserTypeNotFoundException(Long id) {
        super("User type with id='" + id + "' was not found");
    }
}

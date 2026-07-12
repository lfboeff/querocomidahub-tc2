package br.com.fiap.querocomidahub.usertype.domain.exception;

public class UserTypeInUseException extends RuntimeException {

    public UserTypeInUseException(Long id) {
        super("User type with id='" + id + "' cannot be deleted because it is assigned to one or more users");
    }
}

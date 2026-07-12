package br.com.fiap.querocomidahub.usertype.domain.exception;

public class UserTypeIsSystemException extends RuntimeException {

    public UserTypeIsSystemException(Long id) {
        super("User type with id='" + id + "' is a system type and cannot be modified");
    }
}

package br.com.fiap.querocomidahub.user.domain.exception;

public class UserDuplicateEmailException extends RuntimeException {
    public UserDuplicateEmailException(String email) {
        super("User with email='" + email + "' already exists");
    }
}

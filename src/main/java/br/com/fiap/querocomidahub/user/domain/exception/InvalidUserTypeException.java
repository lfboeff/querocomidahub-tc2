package br.com.fiap.querocomidahub.user.domain.exception;

public class InvalidUserTypeException extends RuntimeException {
    public InvalidUserTypeException(Long userTypeId) {
        super("User type with id='" + userTypeId + "' was not found");
    }
}

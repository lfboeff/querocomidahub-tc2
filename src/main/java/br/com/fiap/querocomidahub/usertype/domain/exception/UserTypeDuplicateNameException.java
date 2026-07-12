package br.com.fiap.querocomidahub.usertype.domain.exception;

public class UserTypeDuplicateNameException extends RuntimeException {

    public UserTypeDuplicateNameException(String name) {
        super("User type with name='" + name + "' already exists and cannot be duplicated");
    }
}

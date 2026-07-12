package br.com.fiap.querocomidahub.menuitem.domain.exception;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(Long id) {
        super("Menu item with id='" + id + "' was not found");
    }
}

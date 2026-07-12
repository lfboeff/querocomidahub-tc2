package br.com.fiap.querocomidahub.menuitem.application.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;

public final class MenuItemDTOMapper {

    private MenuItemDTOMapper() {
    }

    public static MenuItemOutputDTO toOutputDTO(MenuItem menuItem) {
        return new MenuItemOutputDTO(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getPhotoPath(),
                menuItem.getCreatedAt(),
                menuItem.getLastModifiedAt()
        );
    }
}

package br.com.fiap.querocomidahub.menuitem.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemRequestJson;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemResponseJson;

public final class MenuItemJSONMapper {

    private MenuItemJSONMapper() {
    }

    public static MenuItemInputDTO toInputDTO(MenuItemRequestJson request) {
        return new MenuItemInputDTO(
                request.name(),
                request.description(),
                request.price(),
                request.dineInOnly(),
                request.photoPath()
        );
    }

    public static MenuItemResponseJson toResponse(MenuItemOutputDTO output) {
        return new MenuItemResponseJson(
                output.id(),
                output.restaurantId(),
                output.name(),
                output.description(),
                output.price(),
                output.dineInOnly(),
                output.photoPath(),
                output.createdAt(),
                output.lastModifiedAt()
        );
    }
}

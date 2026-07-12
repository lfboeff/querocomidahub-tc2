package br.com.fiap.querocomidahub.restaurant.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.menuitem.infrastructure.web.mapper.MenuItemJSONMapper;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantRequestJson;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantResponseJson;

public final class RestaurantJSONMapper {

    private RestaurantJSONMapper() {
    }

    public static RestaurantInputDTO toInputDTO(RestaurantRequestJson request) {
        return new RestaurantInputDTO(
                request.name(),
                request.address(),
                request.kitchenType(),
                request.openingHours()
        );
    }

    public static RestaurantResponseJson toResponse(RestaurantOutputDTO output) {
        return new RestaurantResponseJson(
                output.id(),
                output.name(),
                output.address(),
                output.kitchenType(),
                output.openingHours(),
                output.ownerId(),
                output.createdAt(),
                output.lastModifiedAt(),
                output.menuItems() == null
                        ? null
                        : output.menuItems().stream().map(MenuItemJSONMapper::toResponse).toList()
        );
    }
}

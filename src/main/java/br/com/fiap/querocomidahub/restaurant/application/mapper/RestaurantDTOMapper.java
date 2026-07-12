package br.com.fiap.querocomidahub.restaurant.application.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;

import java.util.List;

public final class RestaurantDTOMapper {

    private RestaurantDTOMapper() {
    }

    public static RestaurantOutputDTO toOutputDTO(Restaurant restaurant) {
        return toOutputDTO(restaurant, null);
    }

    public static RestaurantOutputDTO toOutputDTO(Restaurant restaurant, List<MenuItemOutputDTO> menuItems) {
        return new RestaurantOutputDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getKitchenType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getLastModifiedAt(),
                menuItems
        );
    }
}

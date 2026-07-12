package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.application.mapper.MenuItemDTOMapper;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.application.mapper.RestaurantDTOMapper;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

import java.util.List;

public final class GetRestaurantByIdUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IMenuItemGateway menuItemGateway;
    private final ILoggerGateway logger;

    private GetRestaurantByIdUseCase(IRestaurantGateway restaurantGateway,
                                     IMenuItemGateway menuItemGateway,
                                     ILoggerGateway logger) {
        this.restaurantGateway = restaurantGateway;
        this.menuItemGateway = menuItemGateway;
        this.logger = logger;
    }

    public static GetRestaurantByIdUseCase create(IRestaurantGateway restaurantGateway,
                                                  IMenuItemGateway menuItemGateway,
                                                  ILoggerGateway logger) {
        return new GetRestaurantByIdUseCase(restaurantGateway, menuItemGateway, logger);
    }

    public RestaurantOutputDTO run(Long id) {
        Restaurant restaurant = restaurantGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found", id);
                    throw new RestaurantNotFoundException(id);
                });

        List<MenuItemOutputDTO> menuItems = menuItemGateway.findAllByRestaurantId(restaurant.getId())
                .stream()
                .map(MenuItemDTOMapper::toOutputDTO)
                .toList();

        logger.info("Returning restaurant with id='{}'", restaurant.getId());
        return RestaurantDTOMapper.toOutputDTO(restaurant, menuItems);
    }
}

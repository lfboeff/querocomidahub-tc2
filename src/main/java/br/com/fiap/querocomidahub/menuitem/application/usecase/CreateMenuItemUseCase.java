package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

public final class CreateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private CreateMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway,
                                  ILoggerGateway logger) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static CreateMenuItemUseCase create(IMenuItemGateway menuItemGateway,
                                               IRestaurantGateway restaurantGateway,
                                               ILoggerGateway logger) {
        return new CreateMenuItemUseCase(menuItemGateway, restaurantGateway, logger);
    }

    public Long run(Long restaurantId, MenuItemInputDTO inputDTO, Long callerId) {
        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found during menu item creation", restaurantId);
                    throw new RestaurantNotFoundException(restaurantId);
                });

        if (!restaurant.isOwnedBy(callerId)) {
            logger.warn("User with id='{}' is not the owner of restaurant id='{}' and cannot add menu items",
                    callerId, restaurantId);
            throw new RestaurantAccessDeniedException(callerId, restaurantId);
        }

        MenuItem newMenuItem = MenuItem.create(
                restaurantId,
                inputDTO.name(),
                inputDTO.description(),
                inputDTO.price(),
                inputDTO.dineInOnly(),
                inputDTO.photoPath()
        );

        Long newMenuItemId = menuItemGateway.insert(newMenuItem);

        logger.info("Menu item with id='{}' has been created in restaurant id='{}' by user id='{}'",
                newMenuItemId, restaurantId, callerId);
        return newMenuItemId;
    }
}

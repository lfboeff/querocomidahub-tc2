package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

public final class UpdateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private UpdateMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway,
                                  ILoggerGateway logger) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static UpdateMenuItemUseCase create(IMenuItemGateway menuItemGateway,
                                               IRestaurantGateway restaurantGateway,
                                               ILoggerGateway logger) {
        return new UpdateMenuItemUseCase(menuItemGateway, restaurantGateway, logger);
    }

    public void run(Long restaurantId, Long id, MenuItemInputDTO inputDTO, Long callerId) {
        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found during menu item update", restaurantId);
                    throw new RestaurantNotFoundException(restaurantId);
                });

        if (!restaurant.isOwnedBy(callerId)) {
            logger.warn("User with id='{}' is not the owner of restaurant id='{}' and cannot update its menu items",
                    callerId, restaurantId);
            throw new RestaurantAccessDeniedException(callerId, restaurantId);
        }

        MenuItem existing = menuItemGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Menu item with id='{}' was not found in restaurant id='{}' during update", id, restaurantId);
                    throw new MenuItemNotFoundException(id);
                });

        if (!existing.belongsTo(restaurantId)) {
            logger.warn("Menu item with id='{}' does not belong to restaurant id='{}' during update", id, restaurantId);
            throw new MenuItemNotFoundException(id);
        }

        MenuItem updated = existing.withUpdatedParams(
                inputDTO.name(),
                inputDTO.description(),
                inputDTO.price(),
                inputDTO.dineInOnly(),
                inputDTO.photoPath()
        );

        menuItemGateway.update(updated);

        logger.info("Menu item with id='{}' has been updated by user id='{}'", id, callerId);
    }
}

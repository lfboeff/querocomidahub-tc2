package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

public final class DeleteMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private DeleteMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway,
                                  ILoggerGateway logger) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static DeleteMenuItemUseCase create(IMenuItemGateway menuItemGateway,
                                               IRestaurantGateway restaurantGateway,
                                               ILoggerGateway logger) {
        return new DeleteMenuItemUseCase(menuItemGateway, restaurantGateway, logger);
    }

    public void run(Long restaurantId, Long id, Long callerId) {
        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found during menu item delete", restaurantId);
                    throw new RestaurantNotFoundException(restaurantId);
                });

        if (!restaurant.isOwnedBy(callerId)) {
            logger.warn("User with id='{}' is not the owner of restaurant id='{}' and cannot delete its menu items",
                    callerId, restaurantId);
            throw new RestaurantAccessDeniedException(callerId, restaurantId);
        }

        MenuItem existing = menuItemGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Menu item with id='{}' was not found in restaurant id='{}' during delete", id, restaurantId);
                    throw new MenuItemNotFoundException(id);
                });

        if (!existing.belongsTo(restaurantId)) {
            logger.warn("Menu item with id='{}' does not belong to restaurant id='{}' during delete", id, restaurantId);
            throw new MenuItemNotFoundException(id);
        }

        menuItemGateway.delete(id);

        logger.info("Menu item with id='{}' has been deleted by user id='{}' for restaurant id='{}'",
                id, callerId, restaurantId);
    }
}

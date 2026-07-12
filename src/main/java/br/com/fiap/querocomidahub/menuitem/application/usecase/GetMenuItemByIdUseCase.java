package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

public final class GetMenuItemByIdUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private GetMenuItemByIdUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway,
                                   ILoggerGateway logger) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static GetMenuItemByIdUseCase create(IMenuItemGateway menuItemGateway,
                                                IRestaurantGateway restaurantGateway,
                                                ILoggerGateway logger) {
        return new GetMenuItemByIdUseCase(menuItemGateway, restaurantGateway, logger);
    }

    public MenuItem run(Long restaurantId, Long id) {
        if (restaurantGateway.findById(restaurantId).isEmpty()) {
            logger.warn("Restaurant with id='{}' was not found when fetching menu item id='{}'", restaurantId, id);
            throw new RestaurantNotFoundException(restaurantId);
        }

        MenuItem menuItem = menuItemGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Menu item with id='{}' was not found in restaurant id='{}'", id, restaurantId);
                    throw new MenuItemNotFoundException(id);
                });

        if (!menuItem.belongsTo(restaurantId)) {
            logger.warn("Menu item with id='{}' does not belong to restaurant id='{}'", id, restaurantId);
            throw new MenuItemNotFoundException(id);
        }

        logger.info("Returning menu item with id='{}' from restaurant id='{}'", id, restaurantId);
        return menuItem;
    }
}

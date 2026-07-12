package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

import java.util.List;

public final class ListMenuItemsUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private ListMenuItemsUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway,
                                 ILoggerGateway logger) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static ListMenuItemsUseCase create(IMenuItemGateway menuItemGateway,
                                              IRestaurantGateway restaurantGateway,
                                              ILoggerGateway logger) {
        return new ListMenuItemsUseCase(menuItemGateway, restaurantGateway, logger);
    }

    public List<MenuItem> run(Long restaurantId) {
        if (restaurantGateway.findById(restaurantId).isEmpty()) {
            logger.warn("Restaurant with id='{}' was not found when listing menu items", restaurantId);
            throw new RestaurantNotFoundException(restaurantId);
        }

        List<MenuItem> items = menuItemGateway.findAllByRestaurantId(restaurantId);

        logger.info("Returning all menu items for restaurant id='{}'", restaurantId);
        return items;
    }
}

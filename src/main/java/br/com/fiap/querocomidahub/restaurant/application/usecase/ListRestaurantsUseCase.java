package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

import java.util.List;

public final class ListRestaurantsUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final ILoggerGateway logger;

    private ListRestaurantsUseCase(IRestaurantGateway restaurantGateway, ILoggerGateway logger) {
        this.restaurantGateway = restaurantGateway;
        this.logger = logger;
    }

    public static ListRestaurantsUseCase create(IRestaurantGateway restaurantGateway, ILoggerGateway logger) {
        return new ListRestaurantsUseCase(restaurantGateway, logger);
    }

    public List<Restaurant> run() {
        List<Restaurant> restaurants = restaurantGateway.findAll();

        logger.info("Returning all restaurants");
        return restaurants;
    }
}

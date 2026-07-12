package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantManagementNotAllowedException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class CreateRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private CreateRestaurantUseCase(IRestaurantGateway restaurantGateway, IUserGateway userGateway,
                                    ILoggerGateway logger) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static CreateRestaurantUseCase create(IRestaurantGateway restaurantGateway,
                                                 IUserGateway userGateway, ILoggerGateway logger) {
        return new CreateRestaurantUseCase(restaurantGateway, userGateway, logger);
    }

    public Long run(RestaurantInputDTO inputDTO, Long callerId) {
        UserBase caller = userGateway.findById(callerId)
                .orElseGet(() -> {
                    logger.warn("Caller user with id='{}' was not found during restaurant creation", callerId);
                    throw new UserNotFoundException(callerId);
                });

        if (!caller.canManageRestaurants()) {
            logger.warn("User with id='{}' does not have permission to manage restaurants", caller.getId());
            throw new RestaurantManagementNotAllowedException(caller.getId());
        }

        Restaurant newRestaurant = Restaurant.create(
                inputDTO.name(),
                inputDTO.address(),
                inputDTO.kitchenType(),
                inputDTO.openingHours(),
                caller.getId()
        );

        Long newRestaurantId = restaurantGateway.insert(newRestaurant);

        logger.info("Restaurant with id='{}' has been created by user id='{}'", newRestaurantId, caller.getId());
        return newRestaurantId;
    }
}

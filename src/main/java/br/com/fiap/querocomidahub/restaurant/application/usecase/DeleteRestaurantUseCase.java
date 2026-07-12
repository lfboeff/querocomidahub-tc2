package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class DeleteRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private DeleteRestaurantUseCase(IRestaurantGateway restaurantGateway, IUserGateway userGateway,
                                    ILoggerGateway logger) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static DeleteRestaurantUseCase create(IRestaurantGateway restaurantGateway,
                                                 IUserGateway userGateway, ILoggerGateway logger) {
        return new DeleteRestaurantUseCase(restaurantGateway, userGateway, logger);
    }

    public void run(Long id, Long callerId) {
        UserBase caller = userGateway.findById(callerId)
                .orElseGet(() -> {
                    logger.warn("Caller user with id='{}' was not found during restaurant delete", callerId);
                    throw new UserNotFoundException(callerId);
                });

        Restaurant existing = restaurantGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found during delete", id);
                    throw new RestaurantNotFoundException(id);
                });

        if (!existing.isOwnedBy(caller.getId())) {
            logger.warn("User with id='{}' is not the owner of restaurant id='{}' and cannot delete it",
                    caller.getId(), id);
            throw new RestaurantAccessDeniedException(caller.getId(), id);
        }

        restaurantGateway.delete(id);

        logger.info("Restaurant with id='{}' has been deleted by user id='{}'", id, caller.getId());
    }
}

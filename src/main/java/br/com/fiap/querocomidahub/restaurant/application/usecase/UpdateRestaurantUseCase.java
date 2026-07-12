package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class UpdateRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private UpdateRestaurantUseCase(IRestaurantGateway restaurantGateway, IUserGateway userGateway,
                                    ILoggerGateway logger) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static UpdateRestaurantUseCase create(IRestaurantGateway restaurantGateway,
                                                 IUserGateway userGateway, ILoggerGateway logger) {
        return new UpdateRestaurantUseCase(restaurantGateway, userGateway, logger);
    }

    public void run(Long id, RestaurantInputDTO inputDTO, Long callerId) {
        UserBase caller = userGateway.findById(callerId)
                .orElseGet(() -> {
                    logger.warn("Caller user with id='{}' was not found during restaurant update", callerId);
                    throw new UserNotFoundException(callerId);
                });

        Restaurant existing = restaurantGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("Restaurant with id='{}' was not found during update", id);
                    throw new RestaurantNotFoundException(id);
                });

        if (!existing.isOwnedBy(caller.getId())) {
            logger.warn("User with id='{}' is not the owner of restaurant id='{}' and cannot update it", caller.getId(), id);
            throw new RestaurantAccessDeniedException(caller.getId(), id);
        }

        Restaurant updated = existing.withUpdatedParams(
                inputDTO.name(),
                inputDTO.address(),
                inputDTO.kitchenType(),
                inputDTO.openingHours()
        );

        restaurantGateway.update(updated);

        logger.info("Restaurant with id='{}' has been updated by user id='{}'", id, caller.getId());
    }
}

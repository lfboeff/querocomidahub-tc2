package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserInUseInRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class DeleteUserUseCase {

    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private DeleteUserUseCase(IUserGateway userGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static DeleteUserUseCase create(IUserGateway userGateway, ILoggerGateway logger) {
        return new DeleteUserUseCase(userGateway, logger);
    }

    public void run(Long id) {
        UserBase existingUser = userGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("User with id='{}' was not found during delete", id);
                    throw new UserNotFoundException(id);
                });

        if (userGateway.existsAsRestaurantOwner(existingUser.getId())) {
            logger.warn("User with id='{}' cannot be deleted because they own one or more restaurants", existingUser.getId());
            throw new UserInUseInRestaurantsException(existingUser.getId());
        }

        userGateway.delete(existingUser.getId());

        logger.info("User with id='{}' has been deleted", existingUser.getId());
    }
}

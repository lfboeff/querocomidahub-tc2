package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class GetUserByIdUseCase {

    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private GetUserByIdUseCase(IUserGateway userGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static GetUserByIdUseCase create(IUserGateway userGateway, ILoggerGateway logger) {
        return new GetUserByIdUseCase(userGateway, logger);
    }

    public UserBase run(Long id) {
        UserBase user = userGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("User with id='{}' was not found", id);
                    throw new UserNotFoundException(id);
                });

        logger.info("Returning user with id='{}'", user.getId());
        return user;
    }
}

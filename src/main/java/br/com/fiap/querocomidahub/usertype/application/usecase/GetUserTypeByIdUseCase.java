package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class GetUserTypeByIdUseCase {

    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private GetUserTypeByIdUseCase(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static GetUserTypeByIdUseCase create(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new GetUserTypeByIdUseCase(userTypeGateway, logger);
    }

    public UserType run(Long id) {
        UserType userType = userTypeGateway.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User type with id='{}' was not found", id);
                    return new UserTypeNotFoundException(id);
                });

        logger.info("Returning user type with id='{}'", userType.getId());
        return userType;
    }
}

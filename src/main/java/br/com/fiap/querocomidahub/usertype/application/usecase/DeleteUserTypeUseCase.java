package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeInUseException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class DeleteUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;
    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private DeleteUserTypeUseCase(IUserTypeGateway userTypeGateway, IUserGateway userGateway,
                                  ILoggerGateway logger) {
        this.userTypeGateway = userTypeGateway;
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static DeleteUserTypeUseCase create(IUserTypeGateway userTypeGateway, IUserGateway userGateway,
                                               ILoggerGateway logger) {
        return new DeleteUserTypeUseCase(userTypeGateway, userGateway, logger);
    }

    public void run(Long id) {
        UserType existingUserType = userTypeGateway.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User type with id='{}' was not found and cannot be deleted", id);
                    return new UserTypeNotFoundException(id);
                });

        try {
            existingUserType.ensureModifiable();
        } catch (UserTypeIsSystemException e) {
            logger.warn("User type with id='{}' is a system type and cannot be deleted", id);
            throw e;
        }

        if (userGateway.existsByUserTypeId(existingUserType.getId())) {
            logger.warn("User type with id='{}' cannot be deleted because it is still assigned to users",
                    existingUserType.getId());
            throw new UserTypeInUseException(existingUserType.getId());
        }

        userTypeGateway.delete(existingUserType.getId());

        logger.info("User type with id='{}' has been deleted", existingUserType.getId());
    }
}

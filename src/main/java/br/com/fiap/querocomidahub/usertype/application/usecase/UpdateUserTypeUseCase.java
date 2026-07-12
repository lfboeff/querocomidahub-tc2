package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class UpdateUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private UpdateUserTypeUseCase(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static UpdateUserTypeUseCase create(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new UpdateUserTypeUseCase(userTypeGateway, logger);
    }

    public void run(Long id, UserTypeInputDTO inputDTO) {
        UserType existingUserType = userTypeGateway.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User type with id='{}' was not found and cannot be updated", id);
                    return new UserTypeNotFoundException(id);
                });

        try {
            existingUserType.ensureModifiable();
        } catch (UserTypeIsSystemException e) {
            logger.warn("User type with id='{}' is a system type and cannot be updated", id);
            throw e;
        }

        UserType userTypeToUpdate = existingUserType.withUpdatedParams(inputDTO.name(), inputDTO.canManageRestaurants());

        if (userTypeGateway.existsByNameForDifferentId(userTypeToUpdate.getName(), userTypeToUpdate.getId())) {
            logger.warn("User type name='{}' is already in use by another user type", userTypeToUpdate.getName());
            throw new UserTypeDuplicateNameException(userTypeToUpdate.getName());
        }

        userTypeGateway.update(userTypeToUpdate);

        logger.info("User type with id='{}' has been updated", userTypeToUpdate.getId());
    }
}

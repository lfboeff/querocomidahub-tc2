package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.exception.UserOwnsRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class AssignUserTypeUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private AssignUserTypeUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static AssignUserTypeUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new AssignUserTypeUseCase(userGateway, userTypeGateway, logger);
    }

    public void run(Long userId, AssignUserTypeInputDTO inputDTO) {
        UserBase existingUser = userGateway.findById(userId)
                .orElseGet(() -> {
                    logger.warn("User with id='{}' was not found during user type assignment", userId);
                    throw new UserNotFoundException(userId);
                });

        UserType newUserType = userTypeGateway.findById(inputDTO.userTypeId())
                .orElseGet(() -> {
                    logger.warn("User type with id='{}' is invalid or does not exist during assignment to user id='{}'", inputDTO.userTypeId(), userId);
                    throw new InvalidUserTypeException(inputDTO.userTypeId());
                });

        if (existingUser.hasSameUserType(newUserType)) {
            logger.info("User with id='{}' already has user type id='{}', no change needed", userId, newUserType.getId());
            return;
        }

        if (existingUser.isBeingDemotedFrom(newUserType) && userGateway.existsAsRestaurantOwner(userId)) {
            logger.warn("User with id='{}' cannot be assigned user type id='{}' because they still own one or more restaurants", userId, newUserType.getId());
            throw new UserOwnsRestaurantsException(userId);
        }

        userGateway.updateUserType(userId, newUserType.getId());

        logger.info("User with id='{}' has been assigned user type id='{}'", userId, newUserType.getId());
    }
}

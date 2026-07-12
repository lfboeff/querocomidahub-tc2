package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class CreateUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private CreateUserTypeUseCase(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static CreateUserTypeUseCase create(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new CreateUserTypeUseCase(userTypeGateway, logger);
    }

    public Long run(UserTypeInputDTO inputDTO) {
        UserType newUserType = UserType.create(inputDTO.name(), inputDTO.canManageRestaurants());

        if (userTypeGateway.existsByName(newUserType.getName())) {
            logger.warn("User type name='{}' is already in use and cannot be duplicated", newUserType.getName());
            throw new UserTypeDuplicateNameException(newUserType.getName());
        }

        Long newUserTypeId = userTypeGateway.insert(newUserType);

        logger.info("User type with id='{}' has been created", newUserTypeId);
        return newUserTypeId;
    }
}

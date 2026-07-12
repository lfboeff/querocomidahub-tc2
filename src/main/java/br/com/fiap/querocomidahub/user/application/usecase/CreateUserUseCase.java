package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.user.domain.model.UserFactory;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class CreateUserUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private CreateUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static CreateUserUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new CreateUserUseCase(userGateway, userTypeGateway, logger);
    }

    public Long run(CreateUserInputDTO inputDTO) {
        UserType userType = userTypeGateway.findById(inputDTO.userTypeId())
                .orElseGet(() -> {
                    logger.warn("User type with id='{}' is invalid or does not exist during user creation", inputDTO.userTypeId());
                    throw new InvalidUserTypeException(inputDTO.userTypeId());
                });

        if (userGateway.existsByEmail(inputDTO.email())) {
            logger.warn("User with email='{}' already exists", inputDTO.email());
            throw new UserDuplicateEmailException(inputDTO.email());
        }

        UserBase newUser = UserFactory.create(inputDTO.name(), inputDTO.email(), inputDTO.address(), userType);
        Long newUserId = userGateway.insert(newUser);

        logger.info("User with id='{}' has been created", newUserId);
        return newUserId;
    }
}

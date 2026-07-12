package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

public final class UpdateUserUseCase {

    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private UpdateUserUseCase(IUserGateway userGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static UpdateUserUseCase create(IUserGateway userGateway, ILoggerGateway logger) {
        return new UpdateUserUseCase(userGateway, logger);
    }

    public void run(Long id, UpdateUserInputDTO inputDTO) {
        UserBase existingUser = userGateway.findById(id)
                .orElseGet(() -> {
                    logger.warn("User with id='{}' was not found during update", id);
                    throw new UserNotFoundException(id);
                });

        if (userGateway.existsByEmailForDifferentId(inputDTO.email(), id)) {
            logger.warn("Email is already in use by another user during update of user id='{}'", id);
            throw new UserDuplicateEmailException(inputDTO.email());
        }

        UserBase userToUpdate = existingUser.withUpdatedParams(
                inputDTO.name(),
                inputDTO.email(),
                inputDTO.address()
        );

        userGateway.update(userToUpdate);

        logger.info("User with id='{}' has been updated", userToUpdate.getId());
    }
}

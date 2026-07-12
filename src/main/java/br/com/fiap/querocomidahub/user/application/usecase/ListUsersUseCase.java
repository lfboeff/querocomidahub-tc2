package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

import java.util.List;

public final class ListUsersUseCase {

    private final IUserGateway userGateway;
    private final ILoggerGateway logger;

    private ListUsersUseCase(IUserGateway userGateway, ILoggerGateway logger) {
        this.userGateway = userGateway;
        this.logger = logger;
    }

    public static ListUsersUseCase create(IUserGateway userGateway, ILoggerGateway logger) {
        return new ListUsersUseCase(userGateway, logger);
    }

    public List<UserBase> run() {
        List<UserBase> users = userGateway.findAll();

        logger.info("Returning all users");
        return users;
    }
}

package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.util.List;

public final class ListUserTypesUseCase {

    private final IUserTypeGateway userTypeGateway;
    private final ILoggerGateway logger;

    private ListUserTypesUseCase(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        this.userTypeGateway = userTypeGateway;
        this.logger = logger;
    }

    public static ListUserTypesUseCase create(IUserTypeGateway userTypeGateway, ILoggerGateway logger) {
        return new ListUserTypesUseCase(userTypeGateway, logger);
    }

    public List<UserType> run() {
        List<UserType> userTypes = userTypeGateway.findAll();

        logger.info("Returning all user types");
        return userTypes;
    }
}

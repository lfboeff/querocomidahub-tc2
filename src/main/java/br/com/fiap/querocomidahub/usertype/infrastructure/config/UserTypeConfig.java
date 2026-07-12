package br.com.fiap.querocomidahub.usertype.infrastructure.config;

import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.usertype.application.controller.UserTypeController;
import br.com.fiap.querocomidahub.usertype.application.usecase.CreateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.DeleteUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.GetUserTypeByIdUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.ListUserTypesUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.UpdateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTypeConfig {

    @Bean
    public ListUserTypesUseCase listUserTypesUseCase(IUserTypeGateway userTypeGateway) {
        return ListUserTypesUseCase.create(userTypeGateway, LoggerGatewayFactory.forClass(ListUserTypesUseCase.class));
    }

    @Bean
    public GetUserTypeByIdUseCase getUserTypeByIdUseCase(IUserTypeGateway userTypeGateway) {
        return GetUserTypeByIdUseCase.create(userTypeGateway, LoggerGatewayFactory.forClass(GetUserTypeByIdUseCase.class));
    }

    @Bean
    public CreateUserTypeUseCase createUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        return CreateUserTypeUseCase.create(userTypeGateway, LoggerGatewayFactory.forClass(CreateUserTypeUseCase.class));
    }

    @Bean
    public UpdateUserTypeUseCase updateUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        return UpdateUserTypeUseCase.create(userTypeGateway, LoggerGatewayFactory.forClass(UpdateUserTypeUseCase.class));
    }

    @Bean
    public DeleteUserTypeUseCase deleteUserTypeUseCase(IUserTypeGateway userTypeGateway, IUserGateway userGateway) {
        return DeleteUserTypeUseCase.create(userTypeGateway, userGateway, LoggerGatewayFactory.forClass(DeleteUserTypeUseCase.class));
    }

    @Bean
    public UserTypeController userTypeController(ListUserTypesUseCase listUserTypesUseCase,
                                                 GetUserTypeByIdUseCase getUserTypeByIdUseCase,
                                                 CreateUserTypeUseCase createUserTypeUseCase,
                                                 UpdateUserTypeUseCase updateUserTypeUseCase,
                                                 DeleteUserTypeUseCase deleteUserTypeUseCase) {
        return UserTypeController.create(
                listUserTypesUseCase,
                getUserTypeByIdUseCase,
                createUserTypeUseCase,
                updateUserTypeUseCase,
                deleteUserTypeUseCase
        );
    }
}

package br.com.fiap.querocomidahub.user.infrastructure.config;

import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.application.controller.UserController;
import br.com.fiap.querocomidahub.user.application.usecase.AssignUserTypeUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.CreateUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.DeleteUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.GetUserByIdUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.ListUsersUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.UpdateUserUseCase;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public ListUsersUseCase listUsersUseCase(IUserGateway userGateway) {
        return ListUsersUseCase.create(userGateway, LoggerGatewayFactory.forClass(ListUsersUseCase.class));
    }

    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(IUserGateway userGateway) {
        return GetUserByIdUseCase.create(userGateway, LoggerGatewayFactory.forClass(GetUserByIdUseCase.class));
    }

    @Bean
    public CreateUserUseCase createUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        return CreateUserUseCase.create(userGateway, userTypeGateway, LoggerGatewayFactory.forClass(CreateUserUseCase.class));
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(IUserGateway userGateway) {
        return UpdateUserUseCase.create(userGateway, LoggerGatewayFactory.forClass(UpdateUserUseCase.class));
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(IUserGateway userGateway) {
        return DeleteUserUseCase.create(userGateway, LoggerGatewayFactory.forClass(DeleteUserUseCase.class));
    }

    @Bean
    public AssignUserTypeUseCase assignUserTypeUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        return AssignUserTypeUseCase.create(userGateway, userTypeGateway, LoggerGatewayFactory.forClass(AssignUserTypeUseCase.class));
    }

    @Bean
    public UserController userController(ListUsersUseCase listUsersUseCase,
                                         GetUserByIdUseCase getUserByIdUseCase,
                                         CreateUserUseCase createUserUseCase,
                                         UpdateUserUseCase updateUserUseCase,
                                         DeleteUserUseCase deleteUserUseCase,
                                         AssignUserTypeUseCase assignUserTypeUseCase) {
        return UserController.create(
                listUsersUseCase,
                getUserByIdUseCase,
                createUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                assignUserTypeUseCase
        );
    }
}

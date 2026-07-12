package br.com.fiap.querocomidahub.user.application.controller;

import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.application.mapper.UserDTOMapper;
import br.com.fiap.querocomidahub.user.application.usecase.AssignUserTypeUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.CreateUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.DeleteUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.GetUserByIdUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.ListUsersUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.UpdateUserUseCase;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;

import java.util.List;

public final class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final AssignUserTypeUseCase assignUserTypeUseCase;

    private UserController(ListUsersUseCase listUsersUseCase,
                           GetUserByIdUseCase getUserByIdUseCase,
                           CreateUserUseCase createUserUseCase,
                           UpdateUserUseCase updateUserUseCase,
                           DeleteUserUseCase deleteUserUseCase,
                           AssignUserTypeUseCase assignUserTypeUseCase) {
        this.listUsersUseCase = listUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.assignUserTypeUseCase = assignUserTypeUseCase;
    }

    public static UserController create(ListUsersUseCase listUsersUseCase,
                                        GetUserByIdUseCase getUserByIdUseCase,
                                        CreateUserUseCase createUserUseCase,
                                        UpdateUserUseCase updateUserUseCase,
                                        DeleteUserUseCase deleteUserUseCase,
                                        AssignUserTypeUseCase assignUserTypeUseCase) {
        return new UserController(listUsersUseCase, getUserByIdUseCase, createUserUseCase,
                updateUserUseCase, deleteUserUseCase, assignUserTypeUseCase);
    }

    public List<UserOutputDTO> findAll() {
        return listUsersUseCase.run()
                .stream()
                .map(UserDTOMapper::toOutputDTO)
                .toList();
    }

    public UserOutputDTO findById(Long id) {
        UserBase user = getUserByIdUseCase.run(id);
        return UserDTOMapper.toOutputDTO(user);
    }

    public Long create(CreateUserInputDTO inputDTO) {
        return createUserUseCase.run(inputDTO);
    }

    public void update(Long id, UpdateUserInputDTO inputDTO) {
        updateUserUseCase.run(id, inputDTO);
    }

    public void delete(Long id) {
        deleteUserUseCase.run(id);
    }

    public void assignUserType(Long userId, AssignUserTypeInputDTO inputDTO) {
        assignUserTypeUseCase.run(userId, inputDTO);
    }
}

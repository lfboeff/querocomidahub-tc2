package br.com.fiap.querocomidahub.usertype.application.controller;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.application.mapper.UserTypeDTOMapper;
import br.com.fiap.querocomidahub.usertype.application.usecase.CreateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.DeleteUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.GetUserTypeByIdUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.ListUserTypesUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.UpdateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.util.List;

public final class UserTypeController {

    private final ListUserTypesUseCase listUserTypesUseCase;
    private final GetUserTypeByIdUseCase getUserTypeByIdUseCase;
    private final CreateUserTypeUseCase createUserTypeUseCase;
    private final UpdateUserTypeUseCase updateUserTypeUseCase;
    private final DeleteUserTypeUseCase deleteUserTypeUseCase;

    private UserTypeController(ListUserTypesUseCase listUserTypesUseCase,
                               GetUserTypeByIdUseCase getUserTypeByIdUseCase,
                               CreateUserTypeUseCase createUserTypeUseCase,
                               UpdateUserTypeUseCase updateUserTypeUseCase,
                               DeleteUserTypeUseCase deleteUserTypeUseCase) {
        this.listUserTypesUseCase = listUserTypesUseCase;
        this.getUserTypeByIdUseCase = getUserTypeByIdUseCase;
        this.createUserTypeUseCase = createUserTypeUseCase;
        this.updateUserTypeUseCase = updateUserTypeUseCase;
        this.deleteUserTypeUseCase = deleteUserTypeUseCase;
    }

    public static UserTypeController create(ListUserTypesUseCase listUserTypesUseCase,
                                            GetUserTypeByIdUseCase getUserTypeByIdUseCase,
                                            CreateUserTypeUseCase createUserTypeUseCase,
                                            UpdateUserTypeUseCase updateUserTypeUseCase,
                                            DeleteUserTypeUseCase deleteUserTypeUseCase) {
        return new UserTypeController(
                listUserTypesUseCase,
                getUserTypeByIdUseCase,
                createUserTypeUseCase,
                updateUserTypeUseCase,
                deleteUserTypeUseCase
        );
    }

    public List<UserTypeOutputDTO> findAll() {
        return listUserTypesUseCase.run()
                .stream()
                .map(UserTypeDTOMapper::toOutputDTO)
                .toList();
    }

    public UserTypeOutputDTO findById(Long id) {
        UserType userType = getUserTypeByIdUseCase.run(id);
        return UserTypeDTOMapper.toOutputDTO(userType);
    }

    public Long create(UserTypeInputDTO inputDTO) {
        return createUserTypeUseCase.run(inputDTO);
    }

    public void update(Long id, UserTypeInputDTO inputDTO) {
        updateUserTypeUseCase.run(id, inputDTO);
    }

    public void delete(Long id) {
        deleteUserTypeUseCase.run(id);
    }
}

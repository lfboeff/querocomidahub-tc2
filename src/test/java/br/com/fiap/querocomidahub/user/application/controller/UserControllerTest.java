package br.com.fiap.querocomidahub.user.application.controller;

import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.application.usecase.AssignUserTypeUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.CreateUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.DeleteUserUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.GetUserByIdUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.ListUsersUseCase;
import br.com.fiap.querocomidahub.user.application.usecase.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.CARLOS_DONO;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.userList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;

    @Mock
    private ListUsersUseCase listUsersUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private AssignUserTypeUseCase assignUserTypeUseCase;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = UserController.create(
                listUsersUseCase,
                getUserByIdUseCase,
                createUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                assignUserTypeUseCase);
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void delegates_to_list_use_case_and_maps_each_domain_to_output_dto() {
            when(listUsersUseCase.run()).thenReturn(userList());

            List<UserOutputDTO> result = userController.findAll();

            assertThat(result).hasSize(userList().size());
            assertThat(result)
                    .extracting(UserOutputDTO::id, UserOutputDTO::name, UserOutputDTO::email)
                    .containsExactly(
                            tuple(JOAO_CLIENTE.getId(), JOAO_CLIENTE.getName(), JOAO_CLIENTE.getEmail()),
                            tuple(MARIA_DONA.getId(), MARIA_DONA.getName(), MARIA_DONA.getEmail()),
                            tuple(CARLOS_DONO.getId(), CARLOS_DONO.getName(), CARLOS_DONO.getEmail()));

            verify(listUsersUseCase).run();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void delegates_to_get_by_id_use_case_and_maps_domain_to_output_dto() {
            when(getUserByIdUseCase.run(JOAO_CLIENTE.getId())).thenReturn(JOAO_CLIENTE);

            UserOutputDTO result = userController.findById(JOAO_CLIENTE.getId());

            assertThat(result.id()).isEqualTo(JOAO_CLIENTE.getId());
            assertThat(result.email()).isEqualTo(JOAO_CLIENTE.getEmail());

            verify(getUserByIdUseCase).run(JOAO_CLIENTE.getId());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void delegates_to_create_use_case_and_returns_generated_id() {
            CreateUserInputDTO dto = new CreateUserInputDTO("Name", "a@b.com", "Addr", 1L);
            when(createUserUseCase.run(dto)).thenReturn(99L);

            Long result = userController.create(dto);

            assertThat(result).isEqualTo(99L);

            verify(createUserUseCase).run(dto);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void delegates_to_update_use_case() {
            UpdateUserInputDTO dto = new UpdateUserInputDTO("Name", "a@b.com", "Addr");

            userController.update(1L, dto);

            verify(updateUserUseCase).run(1L, dto);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void delegates_to_delete_use_case() {
            userController.delete(1L);

            verify(deleteUserUseCase).run(1L);
        }
    }

    @Nested
    @DisplayName("assignUserType()")
    class AssignUserTypeTest {

        @Test
        void delegates_to_assign_user_type_use_case() {
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(2L);

            userController.assignUserType(1L, dto);

            verify(assignUserTypeUseCase).run(1L, dto);
        }
    }
}

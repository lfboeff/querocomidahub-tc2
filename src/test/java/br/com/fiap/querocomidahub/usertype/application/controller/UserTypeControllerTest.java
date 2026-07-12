package br.com.fiap.querocomidahub.usertype.application.controller;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.application.usecase.CreateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.DeleteUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.GetUserTypeByIdUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.ListUserTypesUseCase;
import br.com.fiap.querocomidahub.usertype.application.usecase.UpdateUserTypeUseCase;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;
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

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.DONO_DE_RESTAURANTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.MOTOBOY;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.userTypeList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserTypeController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeControllerTest {

    @Mock
    private ListUserTypesUseCase listUseCase;

    @Mock
    private GetUserTypeByIdUseCase getByIdUseCase;

    @Mock
    private CreateUserTypeUseCase createUseCase;

    @Mock
    private UpdateUserTypeUseCase updateUseCase;

    @Mock
    private DeleteUserTypeUseCase deleteUseCase;

    private UserTypeController userTypeController;

    @BeforeEach
    void setUp() {
        userTypeController = UserTypeController.create(
                listUseCase,
                getByIdUseCase,
                createUseCase,
                updateUseCase,
                deleteUseCase);
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void delegates_to_list_use_case_and_maps_each_domain_to_output_dto() {
            List<UserType> userTypes = userTypeList();

            when(listUseCase.run()).thenReturn(userTypes);

            List<UserTypeOutputDTO> result = userTypeController.findAll();

            assertThat(result)
                    .extracting(UserTypeOutputDTO::id, UserTypeOutputDTO::name, UserTypeOutputDTO::canManageRestaurants)
                    .containsExactly(
                            tuple(DONO_DE_RESTAURANTE.getId(), DONO_DE_RESTAURANTE.getName(), DONO_DE_RESTAURANTE.canManageRestaurants()),
                            tuple(CLIENTE.getId(), CLIENTE.getName(), CLIENTE.canManageRestaurants()),
                            tuple(MOTOBOY.getId(), MOTOBOY.getName(), MOTOBOY.canManageRestaurants()));

            verify(listUseCase).run();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void delegates_to_get_by_id_use_case_and_maps_domain_to_output_dto() {
            when(getByIdUseCase.run(CLIENTE.getId())).thenReturn(CLIENTE);

            UserTypeOutputDTO result = userTypeController.findById(CLIENTE.getId());

            assertThat(result.id()).isEqualTo(CLIENTE.getId());
            assertThat(result.name()).isEqualTo(CLIENTE.getName());
            assertThat(result.isSystem()).isEqualTo(CLIENTE.isSystem());
            assertThat(result.canManageRestaurants()).isEqualTo(CLIENTE.canManageRestaurants());
            assertThat(result.createdAt()).isEqualTo(CLIENTE.getCreatedAt());
            assertThat(result.lastModifiedAt()).isEqualTo(CLIENTE.getLastModifiedAt());

            verify(getByIdUseCase).run(CLIENTE.getId());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void delegates_to_create_use_case_and_returns_generated_id() {
            Long id = 99L;
            UserTypeInputDTO dto = new UserTypeInputDTO("Cook", false);

            when(createUseCase.run(dto)).thenReturn(id);

            Long result = userTypeController.create(dto);

            assertThat(result).isEqualTo(id);

            verify(createUseCase).run(dto);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void delegates_to_update_use_case() {
            Long id = MOTOBOY.getId();
            UserTypeInputDTO dto = new UserTypeInputDTO("Cook", false);

            userTypeController.update(id, dto);

            verify(updateUseCase).run(id, dto);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void delegates_to_delete_use_case() {
            Long id = MOTOBOY.getId();

            userTypeController.delete(id);

            verify(deleteUseCase).run(id);
        }
    }
}

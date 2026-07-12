package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
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

import java.util.Optional;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.MOTOBOY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserTypeUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UpdateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @BeforeEach
    void setUp() {
        updateUserTypeUseCase = UpdateUserTypeUseCase.create(userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void updates_successfully_when_type_exists_and_is_not_system_and_name_is_unique() {
            String newName = "A New Name";
            UserTypeInputDTO dto = new UserTypeInputDTO(newName, MOTOBOY.canManageRestaurants());

            when(userTypeGateway.findById(MOTOBOY.getId())).thenReturn(Optional.of(MOTOBOY));
            when(userTypeGateway.existsByNameForDifferentId(newName, MOTOBOY.getId())).thenReturn(false);

            updateUserTypeUseCase.run(MOTOBOY.getId(), dto);

            verify(userTypeGateway).update(any(UserType.class));
        }

        @Test
        void throws_UserTypeNotFoundException_when_id_does_not_exist() {
            Long id = MOTOBOY.getId();
            UserTypeInputDTO dto = new UserTypeInputDTO(MOTOBOY.getName(), MOTOBOY.canManageRestaurants());

            when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateUserTypeUseCase.run(id, dto))
                    .isInstanceOf(UserTypeNotFoundException.class)
                    .hasMessageContaining(String.valueOf(id));

            verify(userTypeGateway, never()).update(any());
        }

        @Test
        void throws_UserTypeIsSystemException_when_type_is_a_system_type() {
            Long id = CLIENTE.getId();
            UserTypeInputDTO dto = new UserTypeInputDTO(CLIENTE.getName(), CLIENTE.canManageRestaurants());

            when(userTypeGateway.findById(id)).thenReturn(Optional.of(CLIENTE));

            assertThatThrownBy(() -> updateUserTypeUseCase.run(id, dto))
                    .isInstanceOf(UserTypeIsSystemException.class)
                    .hasMessageContaining(String.valueOf(id));

            verify(userTypeGateway, never()).update(any());
        }

        @Test
        void throws_UserTypeDuplicateNameException_when_name_is_already_taken_by_another_type() {
            Long id = MOTOBOY.getId();
            String newName = "A New Name";
            UserTypeInputDTO dto = new UserTypeInputDTO(newName, MOTOBOY.canManageRestaurants());

            when(userTypeGateway.findById(id)).thenReturn(Optional.of(MOTOBOY));
            when(userTypeGateway.existsByNameForDifferentId(dto.name(), id)).thenReturn(true);

            assertThatThrownBy(() -> updateUserTypeUseCase.run(id, dto))
                    .isInstanceOf(UserTypeDuplicateNameException.class)
                    .hasMessageContaining(newName);

            verify(userTypeGateway, never()).update(any());
        }
    }
}

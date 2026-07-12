package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeInUseException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
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
@DisplayName("DeleteUserTypeUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeleteUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @BeforeEach
    void setUp() {
        deleteUserTypeUseCase = DeleteUserTypeUseCase.create(userTypeGateway, userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void deletes_successfully_when_type_exists_and_is_not_system_and_is_not_in_use() {
            Long id = MOTOBOY.getId();

            when(userTypeGateway.findById(id)).thenReturn(Optional.of(MOTOBOY));
            when(userGateway.existsByUserTypeId(id)).thenReturn(false);

            deleteUserTypeUseCase.run(id);

            verify(userTypeGateway).delete(id);
        }

        @Test
        void throws_UserTypeNotFoundException_when_id_does_not_exist() {
            Long id = MOTOBOY.getId();

            when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deleteUserTypeUseCase.run(id))
                    .isInstanceOf(UserTypeNotFoundException.class)
                    .hasMessageContaining(String.valueOf(id));

            verify(userTypeGateway, never()).delete(any());
        }

        @Test
        void throws_UserTypeIsSystemException_when_type_is_a_system_type() {
            Long id = CLIENTE.getId();

            when(userTypeGateway.findById(id)).thenReturn(Optional.of(CLIENTE));

            assertThatThrownBy(() -> deleteUserTypeUseCase.run(id))
                    .isInstanceOf(UserTypeIsSystemException.class)
                    .hasMessageContaining(String.valueOf(id));

            verify(userTypeGateway, never()).delete(any());
        }

        @Test
        void throws_UserTypeInUseException_when_type_is_assigned_to_at_least_one_user() {
            Long id = MOTOBOY.getId();

            when(userTypeGateway.findById(id)).thenReturn(Optional.of(MOTOBOY));
            when(userGateway.existsByUserTypeId(id)).thenReturn(true);

            assertThatThrownBy(() -> deleteUserTypeUseCase.run(id))
                    .isInstanceOf(UserTypeInUseException.class)
                    .hasMessageContaining(String.valueOf(id));

            verify(userTypeGateway, never()).delete(any());
        }
    }
}

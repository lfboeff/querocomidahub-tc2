package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserInUseInRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
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

import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeleteUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private DeleteUserUseCase deleteUserUseCase;

    @BeforeEach
    void setUp() {
        deleteUserUseCase = DeleteUserUseCase.create(userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void deletes_successfully_when_user_exists_and_is_not_owner() {
            Long id = JOAO_CLIENTE.getId();

            when(userGateway.findById(id)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userGateway.existsAsRestaurantOwner(id)).thenReturn(false);

            deleteUserUseCase.run(id);

            verify(userGateway).delete(id);
        }

        @Test
        void throws_UserNotFoundException_when_id_does_not_exist() {
            Long id = 99L;

            when(userGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deleteUserUseCase.run(id))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userGateway, never()).delete(any());
        }

        @Test
        void throws_UserInUseInRestaurantsException_when_user_owns_a_restaurant() {
            Long id = MARIA_DONA.getId();

            when(userGateway.findById(id)).thenReturn(Optional.of(MARIA_DONA));
            when(userGateway.existsAsRestaurantOwner(id)).thenReturn(true);

            assertThatThrownBy(() -> deleteUserUseCase.run(id))
                    .isInstanceOf(UserInUseInRestaurantsException.class);

            verify(userGateway, never()).delete(any());
        }
    }
}

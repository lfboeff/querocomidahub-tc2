package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.exception.UserOwnsRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
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

import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.DONO_DE_RESTAURANTE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssignUserTypeUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AssignUserTypeUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private AssignUserTypeUseCase assignUserTypeUseCase;

    @BeforeEach
    void setUp() {
        assignUserTypeUseCase = AssignUserTypeUseCase.create(userGateway, userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void assigns_new_userType_when_user_currently_has_a_different_one() {
            Long userId = JOAO_CLIENTE.getId();
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(DONO_DE_RESTAURANTE.getId());

            when(userGateway.findById(userId)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userTypeGateway.findById(DONO_DE_RESTAURANTE.getId())).thenReturn(Optional.of(DONO_DE_RESTAURANTE));

            assignUserTypeUseCase.run(userId, dto);

            verify(userGateway).updateUserType(userId, DONO_DE_RESTAURANTE.getId());
        }

        @Test
        void does_not_update_when_user_already_has_the_given_userType() {
            Long userId = JOAO_CLIENTE.getId();
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(CLIENTE.getId());

            when(userGateway.findById(userId)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));

            assignUserTypeUseCase.run(userId, dto);

            verify(userGateway, never()).updateUserType(any(), any());
        }

        @Test
        void throws_UserNotFoundException_when_user_does_not_exist() {
            Long userId = 99L;
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(CLIENTE.getId());

            when(userGateway.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> assignUserTypeUseCase.run(userId, dto))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userGateway, never()).updateUserType(any(), any());
        }

        @Test
        void throws_InvalidUserTypeException_when_userType_does_not_exist() {
            Long userId = JOAO_CLIENTE.getId();
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(99L);

            when(userGateway.findById(userId)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userTypeGateway.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> assignUserTypeUseCase.run(userId, dto))
                    .isInstanceOf(InvalidUserTypeException.class);

            verify(userGateway, never()).updateUserType(any(), any());
        }

        @Test
        void throws_UserOwnsRestaurantsException_when_owner_is_demoted_while_still_owning_restaurants() {
            Long userId = MARIA_DONA.getId();
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(CLIENTE.getId());

            when(userGateway.findById(userId)).thenReturn(Optional.of(MARIA_DONA));
            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));
            when(userGateway.existsAsRestaurantOwner(userId)).thenReturn(true);

            assertThatThrownBy(() -> assignUserTypeUseCase.run(userId, dto))
                    .isInstanceOf(UserOwnsRestaurantsException.class);

            verify(userGateway, never()).updateUserType(any(), any());
        }

        @Test
        void demotes_owner_when_they_no_longer_own_any_restaurant() {
            Long userId = MARIA_DONA.getId();
            AssignUserTypeInputDTO dto = new AssignUserTypeInputDTO(CLIENTE.getId());

            when(userGateway.findById(userId)).thenReturn(Optional.of(MARIA_DONA));
            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));
            when(userGateway.existsAsRestaurantOwner(userId)).thenReturn(false);

            assignUserTypeUseCase.run(userId, dto);

            verify(userGateway).updateUserType(userId, CLIENTE.getId());
        }
    }
}

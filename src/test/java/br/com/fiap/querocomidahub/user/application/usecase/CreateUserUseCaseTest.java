package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
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
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreateUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = CreateUserUseCase.create(userGateway, userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void creates_user_and_returns_generated_id() {
            CreateUserInputDTO dto = new CreateUserInputDTO(
                    JOAO_CLIENTE.getName(), JOAO_CLIENTE.getEmail(), JOAO_CLIENTE.getAddress(), CLIENTE.getId());

            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));
            when(userGateway.existsByEmail(dto.email())).thenReturn(false);
            when(userGateway.insert(any(UserBase.class))).thenReturn(JOAO_CLIENTE.getId());

            Long id = createUserUseCase.run(dto);

            assertThat(id).isEqualTo(JOAO_CLIENTE.getId());
            verify(userGateway).insert(any(UserBase.class));
        }

        @Test
        void throws_InvalidUserTypeException_when_userType_does_not_exist() {
            CreateUserInputDTO dto = new CreateUserInputDTO(
                    JOAO_CLIENTE.getName(), JOAO_CLIENTE.getEmail(), JOAO_CLIENTE.getAddress(), 99L);

            when(userTypeGateway.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> createUserUseCase.run(dto))
                    .isInstanceOf(InvalidUserTypeException.class)
                    .hasMessageContaining("99");

            verify(userGateway, never()).insert(any());
        }

        @Test
        void throws_UserDuplicateEmailException_when_email_already_exists() {
            CreateUserInputDTO dto = new CreateUserInputDTO(
                    JOAO_CLIENTE.getName(), JOAO_CLIENTE.getEmail(), JOAO_CLIENTE.getAddress(), CLIENTE.getId());

            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));
            when(userGateway.existsByEmail(dto.email())).thenReturn(true);

            assertThatThrownBy(() -> createUserUseCase.run(dto))
                    .isInstanceOf(UserDuplicateEmailException.class)
                    .hasMessageContaining(dto.email());

            verify(userGateway, never()).insert(any());
        }
    }
}

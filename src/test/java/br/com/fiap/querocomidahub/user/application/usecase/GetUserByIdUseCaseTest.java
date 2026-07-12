package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserByIdUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GetUserByIdUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private GetUserByIdUseCase getUserByIdUseCase;

    @BeforeEach
    void setUp() {
        getUserByIdUseCase = GetUserByIdUseCase.create(userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_user_when_found() {
            when(userGateway.findById(JOAO_CLIENTE.getId())).thenReturn(Optional.of(JOAO_CLIENTE));

            UserBase result = getUserByIdUseCase.run(JOAO_CLIENTE.getId());

            assertThat(result).isEqualTo(JOAO_CLIENTE);
        }

        @Test
        void throws_UserNotFoundException_when_id_does_not_exist() {
            Long id = 99L;

            when(userGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getUserByIdUseCase.run(id))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}

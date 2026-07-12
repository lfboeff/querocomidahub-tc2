package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserTypeByIdUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GetUserTypeByIdUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private GetUserTypeByIdUseCase getUserTypeByIdUseCase;

    @BeforeEach
    void setUp() {
        getUserTypeByIdUseCase = GetUserTypeByIdUseCase.create(userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_user_type_when_found() {
            when(userTypeGateway.findById(CLIENTE.getId())).thenReturn(Optional.of(CLIENTE));

            UserType result = getUserTypeByIdUseCase.run(CLIENTE.getId());

            assertThat(result).isEqualTo(CLIENTE);
        }

        @Test
        void throws_UserTypeNotFoundException_when_id_does_not_exist() {
            Long id = 99L;
            when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getUserTypeByIdUseCase.run(id))
                    .isInstanceOf(UserTypeNotFoundException.class)
                    .hasMessageContaining(String.valueOf(id));
        }
    }
}

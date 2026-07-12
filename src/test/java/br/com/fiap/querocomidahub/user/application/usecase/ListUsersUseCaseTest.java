package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
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

import java.util.List;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.userList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListUsersUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListUsersUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private ListUsersUseCase listUsersUseCase;

    @BeforeEach
    void setUp() {
        listUsersUseCase = ListUsersUseCase.create(userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_all_users_from_gateway() {
            when(userGateway.findAll()).thenReturn(userList());

            List<UserBase> result = listUsersUseCase.run();

            assertThat(result).containsExactlyElementsOf(userList());
            verify(userGateway).findAll();
        }

        @Test
        void returns_empty_list_when_no_users_exist() {
            when(userGateway.findAll()).thenReturn(List.of());

            List<UserBase> result = listUsersUseCase.run();

            assertThat(result).isEmpty();
        }
    }
}

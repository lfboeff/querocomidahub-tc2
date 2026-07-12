package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
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

import java.util.List;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.userTypeList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListUserTypesUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListUserTypesUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private ListUserTypesUseCase listUserTypesUseCase;

    @BeforeEach
    void setUp() {
        listUserTypesUseCase = ListUserTypesUseCase.create(userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_all_user_types_from_gateway() {
            List<UserType> userTypes = userTypeList();

            when(userTypeGateway.findAll()).thenReturn(userTypes);

            List<UserType> result = listUserTypesUseCase.run();

            assertThat(result).containsExactlyElementsOf(userTypes);
        }

        @Test
        void returns_empty_list_when_no_user_types_exist() {
            when(userTypeGateway.findAll()).thenReturn(List.of());

            List<UserType> result = listUserTypesUseCase.run();

            assertThat(result).isEmpty();
        }
    }
}

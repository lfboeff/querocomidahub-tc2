package br.com.fiap.querocomidahub.usertype.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
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

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.MOTOBOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserTypeUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private ILoggerGateway logger;

    private CreateUserTypeUseCase createUserTypeUseCase;

    @BeforeEach
    void setUp() {
        createUserTypeUseCase = CreateUserTypeUseCase.create(userTypeGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void creates_user_type_and_returns_generated_id() {
            UserTypeInputDTO dto = new UserTypeInputDTO(MOTOBOY.getName(), MOTOBOY.canManageRestaurants());

            when(userTypeGateway.existsByName(MOTOBOY.getName())).thenReturn(false);
            when(userTypeGateway.insert(any(UserType.class))).thenReturn(MOTOBOY.getId());

            Long id = createUserTypeUseCase.run(dto);

            assertThat(id).isEqualTo(MOTOBOY.getId());

            verify(userTypeGateway).insert(any(UserType.class));
        }

        @Test
        void throws_UserTypeDuplicateNameException_when_name_already_exists() {
            UserTypeInputDTO dto = new UserTypeInputDTO(MOTOBOY.getName(), MOTOBOY.canManageRestaurants());

            when(userTypeGateway.existsByName(MOTOBOY.getName())).thenReturn(true);

            assertThatThrownBy(() -> createUserTypeUseCase.run(dto))
                    .isInstanceOf(UserTypeDuplicateNameException.class)
                    .hasMessageContaining(MOTOBOY.getName());

            verify(userTypeGateway, never()).insert(any());
        }
    }
}

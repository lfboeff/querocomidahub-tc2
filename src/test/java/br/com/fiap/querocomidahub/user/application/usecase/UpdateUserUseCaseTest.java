package br.com.fiap.querocomidahub.user.application.usecase;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UpdateUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private UpdateUserUseCase updateUserUseCase;

    @BeforeEach
    void setUp() {
        updateUserUseCase = UpdateUserUseCase.create(userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void updates_successfully_when_user_exists_and_email_is_unique() {
            Long id = JOAO_CLIENTE.getId();
            UpdateUserInputDTO dto = new UpdateUserInputDTO("New Name", "new@email.com", "New Address");

            when(userGateway.findById(id)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userGateway.existsByEmailForDifferentId(dto.email(), id)).thenReturn(false);

            updateUserUseCase.run(id, dto);

            verify(userGateway).update(any(UserBase.class));
        }

        @Test
        void throws_UserNotFoundException_when_id_does_not_exist() {
            Long id = 99L;
            UpdateUserInputDTO dto = new UpdateUserInputDTO("Name", "a@b.com", "Addr");

            when(userGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateUserUseCase.run(id, dto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");

            verify(userGateway, never()).update(any());
        }

        @Test
        void throws_UserDuplicateEmailException_when_email_is_used_by_another_user() {
            Long id = JOAO_CLIENTE.getId();
            UpdateUserInputDTO dto = new UpdateUserInputDTO("Name", "taken@email.com", "Addr");

            when(userGateway.findById(id)).thenReturn(Optional.of(JOAO_CLIENTE));
            when(userGateway.existsByEmailForDifferentId(dto.email(), id)).thenReturn(true);

            assertThatThrownBy(() -> updateUserUseCase.run(id, dto))
                    .isInstanceOf(UserDuplicateEmailException.class);

            verify(userGateway, never()).update(any());
        }
    }
}

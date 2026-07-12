package br.com.fiap.querocomidahub.shared.infrastructure.security;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserIdentityResolver")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserIdentityResolverTest {

    @Mock
    private IUserGateway userGateway;

    private UserIdentityResolver userIdentityResolver;

    @BeforeEach
    void setUp() {
        userIdentityResolver = new UserIdentityResolver(userGateway);
    }

    @Nested
    @DisplayName("resolve()")
    class ResolveTest {

        @Test
        void returns_user_when_header_is_a_valid_existing_id() {
            when(userGateway.findById(MARIA_DONA.getId())).thenReturn(Optional.of(MARIA_DONA));

            UserBase resolved = userIdentityResolver.resolve(String.valueOf(MARIA_DONA.getId()));

            assertThat(resolved).isEqualTo(MARIA_DONA);

            verify(userGateway).findById(MARIA_DONA.getId());
        }

        @Test
        void trims_surrounding_whitespace_before_parsing_the_id() {
            when(userGateway.findById(MARIA_DONA.getId())).thenReturn(Optional.of(MARIA_DONA));

            UserBase resolved = userIdentityResolver.resolve("  " + MARIA_DONA.getId() + "  ");

            assertThat(resolved).isEqualTo(MARIA_DONA);

            verify(userGateway).findById(MARIA_DONA.getId());
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        void throws_401_when_header_is_missing_or_blank(String headerValue) {
            assertThatThrownBy(() -> userIdentityResolver.resolve(headerValue))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(userGateway, never()).findById(any());
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"abc", "1x", "2.5", "--", "1,0"})
        void throws_401_when_header_is_not_numeric(String headerValue) {
            assertThatThrownBy(() -> userIdentityResolver.resolve(headerValue))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.UNAUTHORIZED));

            verify(userGateway, never()).findById(any());
        }

        @Test
        void throws_UserNotFoundException_when_id_is_valid_but_user_does_not_exist() {
            Long unknownId = 999L;
            String userId = String.valueOf(unknownId);
            when(userGateway.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userIdentityResolver.resolve(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .satisfies(ex -> verify(userGateway).findById(unknownId));
        }
    }
}

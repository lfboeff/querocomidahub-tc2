package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
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

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.CARLOS_DONO;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteRestaurantUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeleteRestaurantUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    @BeforeEach
    void setUp() {
        deleteRestaurantUseCase = DeleteRestaurantUseCase.create(restaurantGateway, userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void deletes_successfully_when_caller_is_owner() {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();

            when(userGateway.findById(MARIA_DONA.getId())).thenReturn(Optional.of(MARIA_DONA));
            when(restaurantGateway.findById(id)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));

            deleteRestaurantUseCase.run(id, MARIA_DONA.getId());

            verify(restaurantGateway).delete(id);
        }

        @Test
        void throws_UserNotFoundException_when_caller_does_not_exist() {
            Long callerId = 99L;
            when(userGateway.findById(callerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deleteRestaurantUseCase.run(1L, callerId))
                    .isInstanceOf(UserNotFoundException.class);

            verify(restaurantGateway, never()).delete(any());
        }

        @Test
        void throws_RestaurantNotFoundException_when_restaurant_does_not_exist() {
            Long id = 99L;
            Long callerId = MARIA_DONA.getId();

            when(userGateway.findById(callerId)).thenReturn(Optional.of(MARIA_DONA));
            when(restaurantGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deleteRestaurantUseCase.run(id, callerId))
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .satisfies(ex -> verify(restaurantGateway, never()).delete(any()));
        }

        @Test
        void throws_RestaurantAccessDeniedException_when_caller_is_not_the_owner() {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();
            Long callerId = CARLOS_DONO.getId();

            when(userGateway.findById(callerId)).thenReturn(Optional.of(CARLOS_DONO));
            when(restaurantGateway.findById(id)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));

            assertThatThrownBy(() -> deleteRestaurantUseCase.run(id, callerId))
                    .isInstanceOf(RestaurantAccessDeniedException.class)
                    .satisfies(ex -> verify(restaurantGateway, never()).delete(any()));
        }
    }
}

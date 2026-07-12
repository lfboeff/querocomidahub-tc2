package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantManagementNotAllowedException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateRestaurantUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private IUserGateway userGateway;

    @Mock
    private ILoggerGateway logger;

    private CreateRestaurantUseCase createRestaurantUseCase;

    @BeforeEach
    void setUp() {
        createRestaurantUseCase = CreateRestaurantUseCase.create(restaurantGateway, userGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void creates_restaurant_when_caller_is_valid_owner() {
            RestaurantInputDTO dto = new RestaurantInputDTO(
                    PIZZARIA_BELLA_NAPOLI.getName(),
                    PIZZARIA_BELLA_NAPOLI.getAddress(),
                    PIZZARIA_BELLA_NAPOLI.getKitchenType(),
                    PIZZARIA_BELLA_NAPOLI.getOpeningHours()
            );

            when(userGateway.findById(MARIA_DONA.getId())).thenReturn(Optional.of(MARIA_DONA));
            when(restaurantGateway.insert(any(Restaurant.class))).thenReturn(PIZZARIA_BELLA_NAPOLI.getId());

            Long id = createRestaurantUseCase.run(dto, MARIA_DONA.getId());

            assertThat(id).isEqualTo(PIZZARIA_BELLA_NAPOLI.getId());

            ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);

            verify(restaurantGateway).insert(captor.capture());

            assertThat(captor.getValue())
                    .extracting(Restaurant::getName, Restaurant::getAddress,
                            Restaurant::getKitchenType, Restaurant::getOpeningHours, Restaurant::getOwnerId)
                    .containsExactly(dto.name(), dto.address(), dto.kitchenType(), dto.openingHours(),
                            MARIA_DONA.getId());
        }

        @Test
        void throws_UserNotFoundException_when_caller_does_not_exist() {
            RestaurantInputDTO dto = new RestaurantInputDTO("N", "A", "K", "H");
            Long callerId = 99L;

            when(userGateway.findById(callerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> createRestaurantUseCase.run(dto, callerId))
                    .isInstanceOf(UserNotFoundException.class);

            verify(restaurantGateway, never()).insert(any());
        }

        @Test
        void throws_RestaurantManagementNotAllowedException_when_caller_is_a_client() {
            RestaurantInputDTO dto = new RestaurantInputDTO("N", "A", "K", "H");
            Long callerId = JOAO_CLIENTE.getId();

            when(userGateway.findById(callerId)).thenReturn(Optional.of(JOAO_CLIENTE));

            assertThatThrownBy(() -> createRestaurantUseCase.run(dto, callerId))
                    .isInstanceOf(RestaurantManagementNotAllowedException.class)
                    .satisfies(ex -> verify(restaurantGateway, never()).insert(any()));
        }
    }
}

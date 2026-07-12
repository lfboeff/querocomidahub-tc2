package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
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

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.restaurantList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListRestaurantsUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListRestaurantsUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private ILoggerGateway logger;

    private ListRestaurantsUseCase listRestaurantsUseCase;

    @BeforeEach
    void setUp() {
        listRestaurantsUseCase = ListRestaurantsUseCase.create(restaurantGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_all_restaurants_from_gateway() {
            when(restaurantGateway.findAll()).thenReturn(restaurantList());

            List<Restaurant> result = listRestaurantsUseCase.run();

            assertThat(result).containsExactlyElementsOf(restaurantList());
        }

        @Test
        void returns_empty_list_when_no_restaurants_exist() {
            when(restaurantGateway.findAll()).thenReturn(List.of());

            List<Restaurant> result = listRestaurantsUseCase.run();

            assertThat(result).isEmpty();
        }
    }
}

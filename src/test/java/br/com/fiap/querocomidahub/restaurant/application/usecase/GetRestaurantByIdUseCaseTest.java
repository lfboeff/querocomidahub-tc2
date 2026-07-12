package br.com.fiap.querocomidahub.restaurant.application.usecase;

import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
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
import java.util.Optional;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetRestaurantByIdUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GetRestaurantByIdUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private ILoggerGateway logger;

    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @BeforeEach
    void setUp() {
        getRestaurantByIdUseCase = GetRestaurantByIdUseCase.create(restaurantGateway, menuItemGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_restaurant_with_empty_menu_items_when_no_items_exist() {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();

            when(restaurantGateway.findById(id)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findAllByRestaurantId(id)).thenReturn(List.of());

            RestaurantOutputDTO result = getRestaurantByIdUseCase.run(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.menuItems()).isEmpty();
        }

        @Test
        void returns_restaurant_with_menu_items_embedded_when_items_exist() {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();
            MenuItem item = MenuItem.reconstitute(1L, id, "Pizza", "desc", new java.math.BigDecimal("29.90"),
                    false, "/img/x.jpg",
                    br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.NOW,
                    br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.NOW);

            when(restaurantGateway.findById(id)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findAllByRestaurantId(id)).thenReturn(List.of(item));

            RestaurantOutputDTO result = getRestaurantByIdUseCase.run(id);

            assertThat(result.menuItems()).hasSize(1);
            assertThat(result.menuItems().get(0).name()).isEqualTo("Pizza");
        }

        @Test
        void throws_RestaurantNotFoundException_when_id_does_not_exist() {
            Long id = 99L;
            when(restaurantGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getRestaurantByIdUseCase.run(id))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }
    }
}

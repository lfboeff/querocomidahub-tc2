package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
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

import java.math.BigDecimal;
import java.util.Optional;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.NOW;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMenuItemByIdUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GetMenuItemByIdUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private ILoggerGateway logger;

    private GetMenuItemByIdUseCase getMenuItemByIdUseCase;

    @BeforeEach
    void setUp() {
        getMenuItemByIdUseCase = GetMenuItemByIdUseCase.create(menuItemGateway, restaurantGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_menu_item_when_found_and_belongs_to_restaurant() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long id = PIZZA_MARGHERITA.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(id)).thenReturn(Optional.of(PIZZA_MARGHERITA));

            MenuItem result = getMenuItemByIdUseCase.run(restaurantId, id);

            assertThat(result).isEqualTo(PIZZA_MARGHERITA);
        }

        @Test
        void throws_RestaurantNotFoundException_when_restaurant_does_not_exist() {
            Long restaurantId = 99L;

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getMenuItemByIdUseCase.run(restaurantId, 1L))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }

        @Test
        void throws_MenuItemNotFoundException_when_menu_item_does_not_exist() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long id = 99L;

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getMenuItemByIdUseCase.run(restaurantId, id))
                    .isInstanceOf(MenuItemNotFoundException.class);
        }

        @Test
        void throws_MenuItemNotFoundException_when_menu_item_belongs_to_a_different_restaurant() {
            Long callerRestaurantId = 2L;
            Long id = PIZZA_MARGHERITA.getId();

            MenuItem itemFromOtherRestaurant = MenuItem.reconstitute(id, 1L, "N", "D",
                    new BigDecimal("10.00"), false, null, NOW, NOW);

            when(restaurantGateway.findById(callerRestaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(id)).thenReturn(Optional.of(itemFromOtherRestaurant));

            assertThatThrownBy(() -> getMenuItemByIdUseCase.run(callerRestaurantId, id))
                    .isInstanceOf(MenuItemNotFoundException.class);
        }
    }
}

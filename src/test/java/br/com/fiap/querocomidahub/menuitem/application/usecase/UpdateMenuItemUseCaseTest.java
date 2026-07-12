package br.com.fiap.querocomidahub.menuitem.application.usecase;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
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
import static br.com.fiap.querocomidahub.user.UserTestFixtures.CARLOS_DONO;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateMenuItemUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UpdateMenuItemUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private ILoggerGateway logger;

    private UpdateMenuItemUseCase updateMenuItemUseCase;

    private static final MenuItemInputDTO VALID_INPUT = new MenuItemInputDTO(
            "New Name", "New Desc", new BigDecimal("50.00"), true, null);

    @BeforeEach
    void setUp() {
        updateMenuItemUseCase = UpdateMenuItemUseCase.create(menuItemGateway, restaurantGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void updates_successfully_when_caller_is_owner_and_item_belongs_to_restaurant() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long id = PIZZA_MARGHERITA.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(id)).thenReturn(Optional.of(PIZZA_MARGHERITA));

            updateMenuItemUseCase.run(restaurantId, id, VALID_INPUT, MARIA_DONA.getId());

            verify(menuItemGateway).update(any(MenuItem.class));
        }

        @Test
        void throws_RestaurantNotFoundException_when_restaurant_does_not_exist() {
            Long restaurantId = 99L;
            Long itemId = 1L;
            Long callerId = MARIA_DONA.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateMenuItemUseCase.run(restaurantId, itemId, VALID_INPUT, callerId))
                    .isInstanceOf(RestaurantNotFoundException.class)
                    .satisfies(ex -> verify(menuItemGateway, never()).update(any()));
        }

        @Test
        void throws_RestaurantAccessDeniedException_when_caller_is_not_the_owner() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long itemId = 1L;
            Long callerId = CARLOS_DONO.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));

            assertThatThrownBy(() -> updateMenuItemUseCase.run(restaurantId, itemId, VALID_INPUT, callerId))
                    .isInstanceOf(RestaurantAccessDeniedException.class)
                    .satisfies(ex -> verify(menuItemGateway, never()).update(any()));
        }

        @Test
        void throws_MenuItemNotFoundException_when_menu_item_does_not_exist() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long itemId = 99L;
            Long callerId = MARIA_DONA.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(itemId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateMenuItemUseCase.run(restaurantId, itemId, VALID_INPUT, callerId))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .satisfies(ex -> verify(menuItemGateway, never()).update(any()));
        }

        @Test
        void throws_MenuItemNotFoundException_when_menu_item_belongs_to_a_different_restaurant() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();
            Long itemId = 5L;
            Long callerId = MARIA_DONA.getId();

            MenuItem itemFromOtherRestaurant = MenuItem.reconstitute(itemId, 99L, "n", "d",
                    new BigDecimal("10.00"), false, null, NOW, NOW);

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findById(itemId)).thenReturn(Optional.of(itemFromOtherRestaurant));

            assertThatThrownBy(() -> updateMenuItemUseCase.run(restaurantId, itemId, VALID_INPUT, callerId))
                    .isInstanceOf(MenuItemNotFoundException.class)
                    .satisfies(ex -> verify(menuItemGateway, never()).update(any()));
        }
    }
}

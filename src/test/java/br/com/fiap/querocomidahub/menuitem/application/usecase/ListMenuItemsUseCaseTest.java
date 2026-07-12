package br.com.fiap.querocomidahub.menuitem.application.usecase;

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

import java.util.List;
import java.util.Optional;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.menuItemList;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListMenuItemsUseCase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListMenuItemsUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private ILoggerGateway logger;

    private ListMenuItemsUseCase listMenuItemsUseCase;

    @BeforeEach
    void setUp() {
        listMenuItemsUseCase = ListMenuItemsUseCase.create(menuItemGateway, restaurantGateway, logger);
    }

    @Nested
    @DisplayName("run()")
    class RunTest {

        @Test
        void returns_all_menu_items_of_the_restaurant() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findAllByRestaurantId(restaurantId)).thenReturn(menuItemList());

            List<MenuItem> result = listMenuItemsUseCase.run(restaurantId);

            assertThat(result).containsExactlyElementsOf(menuItemList());
        }

        @Test
        void returns_empty_list_when_restaurant_has_no_items() {
            Long restaurantId = PIZZARIA_BELLA_NAPOLI.getId();

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(PIZZARIA_BELLA_NAPOLI));
            when(menuItemGateway.findAllByRestaurantId(restaurantId)).thenReturn(List.of());

            List<MenuItem> result = listMenuItemsUseCase.run(restaurantId);

            assertThat(result).isEmpty();
        }

        @Test
        void throws_RestaurantNotFoundException_when_restaurant_does_not_exist() {
            Long restaurantId = 99L;

            when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listMenuItemsUseCase.run(restaurantId))
                    .isInstanceOf(RestaurantNotFoundException.class);
        }
    }
}

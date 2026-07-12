package br.com.fiap.querocomidahub.restaurant.application.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.NOW;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RestaurantDTOMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantDTOMapperTest {

    @Nested
    @DisplayName("toOutputDTO() without menu items")
    class ToOutputDTOWithoutItemsTest {

        @Test
        void maps_restaurant_fields_and_leaves_menu_items_null() {
            RestaurantOutputDTO dto = RestaurantDTOMapper.toOutputDTO(PIZZARIA_BELLA_NAPOLI);

            assertThat(dto.id()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getId());
            assertThat(dto.name()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getName());
            assertThat(dto.address()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getAddress());
            assertThat(dto.kitchenType()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getKitchenType());
            assertThat(dto.openingHours()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getOpeningHours());
            assertThat(dto.ownerId()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getOwnerId());
            assertThat(dto.menuItems()).isNull();
        }
    }

    @Nested
    @DisplayName("toOutputDTO() with menu items")
    class ToOutputDTOWithItemsTest {

        @Test
        void maps_restaurant_and_embeds_menu_items() {
            MenuItemOutputDTO item = new MenuItemOutputDTO(
                    1L, 1L, "Pizza", "desc", new BigDecimal("29.90"), false, "/img/x.jpg", NOW, NOW);

            RestaurantOutputDTO dto = RestaurantDTOMapper.toOutputDTO(PIZZARIA_BELLA_NAPOLI, List.of(item));

            assertThat(dto.menuItems()).hasSize(1);
            assertThat(dto.menuItems().getFirst().name()).isEqualTo("Pizza");
        }
    }
}

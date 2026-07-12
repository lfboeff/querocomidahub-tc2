package br.com.fiap.querocomidahub.menuitem.application.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MenuItemDTOMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemDTOMapperTest {

    @Nested
    @DisplayName("toOutputDTO()")
    class ToOutputDTOTest {

        @Test
        void maps_all_fields_from_domain_to_output_dto() {
            MenuItemOutputDTO dto = MenuItemDTOMapper.toOutputDTO(PIZZA_MARGHERITA);

            assertThat(dto.id()).isEqualTo(PIZZA_MARGHERITA.getId());
            assertThat(dto.restaurantId()).isEqualTo(PIZZA_MARGHERITA.getRestaurantId());
            assertThat(dto.name()).isEqualTo(PIZZA_MARGHERITA.getName());
            assertThat(dto.description()).isEqualTo(PIZZA_MARGHERITA.getDescription());
            assertThat(dto.price()).isEqualByComparingTo(PIZZA_MARGHERITA.getPrice());
            assertThat(dto.dineInOnly()).isEqualTo(PIZZA_MARGHERITA.isDineInOnly());
            assertThat(dto.photoPath()).isEqualTo(PIZZA_MARGHERITA.getPhotoPath());
            assertThat(dto.createdAt()).isEqualTo(PIZZA_MARGHERITA.getCreatedAt());
            assertThat(dto.lastModifiedAt()).isEqualTo(PIZZA_MARGHERITA.getLastModifiedAt());
        }
    }
}

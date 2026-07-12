package br.com.fiap.querocomidahub.menuitem.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemRequestJson;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemResponseJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.toOutputDTO;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MenuItemJSONMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemJSONMapperTest {

    @Nested
    @DisplayName("toInputDTO()")
    class ToInputDTOTest {

        @Test
        void maps_all_fields_from_request_json_to_input_dto() {
            MenuItemRequestJson request = new MenuItemRequestJson(
                    "Pizza", "Delicious", new BigDecimal("29.90"), true, "/img.jpg");

            MenuItemInputDTO dto = MenuItemJSONMapper.toInputDTO(request);

            assertThat(dto.name()).isEqualTo("Pizza");
            assertThat(dto.description()).isEqualTo("Delicious");
            assertThat(dto.price()).isEqualByComparingTo("29.90");
            assertThat(dto.dineInOnly()).isTrue();
            assertThat(dto.photoPath()).isEqualTo("/img.jpg");
        }
    }

    @Nested
    @DisplayName("toResponse()")
    class ToResponseTest {

        @Test
        void maps_output_dto_to_response_json_preserving_all_fields() {
            MenuItemOutputDTO output = toOutputDTO(PIZZA_MARGHERITA);

            MenuItemResponseJson response = MenuItemJSONMapper.toResponse(output);

            assertThat(response.id()).isEqualTo(output.id());
            assertThat(response.restaurantId()).isEqualTo(output.restaurantId());
            assertThat(response.name()).isEqualTo(output.name());
            assertThat(response.description()).isEqualTo(output.description());
            assertThat(response.price()).isEqualByComparingTo(output.price());
            assertThat(response.dineInOnly()).isEqualTo(output.dineInOnly());
            assertThat(response.photoPath()).isEqualTo(output.photoPath());
            assertThat(response.createdAt()).isEqualTo(output.createdAt());
            assertThat(response.lastModifiedAt()).isEqualTo(output.lastModifiedAt());
        }
    }
}

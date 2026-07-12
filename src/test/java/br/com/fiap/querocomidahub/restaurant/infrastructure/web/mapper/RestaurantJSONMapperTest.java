package br.com.fiap.querocomidahub.restaurant.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantRequestJson;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantResponseJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.toOutputDTO;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RestaurantJSONMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantJSONMapperTest {

    @Nested
    @DisplayName("toInputDTO()")
    class ToInputDTOTest {

        @Test
        void maps_all_fields_from_request_json_to_input_dto() {
            RestaurantRequestJson request = new RestaurantRequestJson(
                    "Pizzaria", "Av. X", "Italiana", "Seg-Sex 11h-22h");

            RestaurantInputDTO dto = RestaurantJSONMapper.toInputDTO(request);

            assertThat(dto.name()).isEqualTo("Pizzaria");
            assertThat(dto.address()).isEqualTo("Av. X");
            assertThat(dto.kitchenType()).isEqualTo("Italiana");
            assertThat(dto.openingHours()).isEqualTo("Seg-Sex 11h-22h");
        }
    }

    @Nested
    @DisplayName("toResponse()")
    class ToResponseTest {

        @Test
        void maps_output_dto_to_response_json_preserving_all_fields() {
            RestaurantOutputDTO output = toOutputDTO(PIZZARIA_BELLA_NAPOLI);

            RestaurantResponseJson response = RestaurantJSONMapper.toResponse(output);

            assertThat(response.id()).isEqualTo(output.id());
            assertThat(response.name()).isEqualTo(output.name());
            assertThat(response.address()).isEqualTo(output.address());
            assertThat(response.kitchenType()).isEqualTo(output.kitchenType());
            assertThat(response.openingHours()).isEqualTo(output.openingHours());
            assertThat(response.ownerId()).isEqualTo(output.ownerId());
            assertThat(response.menuItems()).isNull();
        }
    }
}

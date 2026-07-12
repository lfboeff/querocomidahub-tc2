package br.com.fiap.querocomidahub.usertype.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeRequestJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeResponseJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserTypeJSONMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeJSONMapperTest {

    @Nested
    @DisplayName("toInputDTO()")
    class ToInputDTOTest {

        @Test
        void maps_all_fields_from_request_json_to_input_dto() {
            UserTypeRequestJson requestJson = new UserTypeRequestJson(
                    CLIENTE.getName(),
                    CLIENTE.canManageRestaurants()
            );

            UserTypeInputDTO inputDTO = UserTypeJSONMapper.toInputDTO(requestJson);

            assertThat(inputDTO.name()).isEqualTo(CLIENTE.getName());
            assertThat(inputDTO.canManageRestaurants()).isEqualTo(CLIENTE.canManageRestaurants());
        }
    }

    @Nested
    @DisplayName("toResponseJson()")
    class ToResponseJSONTest {

        @Test
        void maps_all_fields_from_output_dto_to_response_json() {
            UserTypeOutputDTO outputDTO = new UserTypeOutputDTO(
                    CLIENTE.getId(),
                    CLIENTE.getName(),
                    CLIENTE.isSystem(),
                    CLIENTE.canManageRestaurants(),
                    NOW,
                    NOW
            );

            UserTypeResponseJson responseJson = UserTypeJSONMapper.toResponseJson(outputDTO);

            assertThat(responseJson.id()).isEqualTo(CLIENTE.getId());
            assertThat(responseJson.name()).isEqualTo(CLIENTE.getName());
            assertThat(responseJson.isSystem()).isEqualTo(CLIENTE.isSystem());
            assertThat(responseJson.canManageRestaurants()).isEqualTo(CLIENTE.canManageRestaurants());
            assertThat(responseJson.createdAt()).isEqualTo(NOW);
            assertThat(responseJson.lastModifiedAt()).isEqualTo(NOW);
        }
    }
}

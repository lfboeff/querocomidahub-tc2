package br.com.fiap.querocomidahub.user.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.AssignUserTypeRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.CreateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UpdateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UserResponseJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.toOutputDTO;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserJSONMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserJSONMapperTest {

    @Nested
    @DisplayName("toCreateInput()")
    class ToCreateInputTest {

        @Test
        void maps_all_fields_from_request_json_to_input_dto() {
            CreateUserRequestJson request = new CreateUserRequestJson("Name", "a@b.com", "Addr", 5L);

            CreateUserInputDTO dto = UserJSONMapper.toCreateInput(request);

            assertThat(dto.name()).isEqualTo("Name");
            assertThat(dto.email()).isEqualTo("a@b.com");
            assertThat(dto.address()).isEqualTo("Addr");
            assertThat(dto.userTypeId()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("toUpdateInput()")
    class ToUpdateInputTest {

        @Test
        void maps_all_fields_from_request_json_to_input_dto() {
            UpdateUserRequestJson request = new UpdateUserRequestJson("Name", "a@b.com", "Addr");

            UpdateUserInputDTO dto = UserJSONMapper.toUpdateInput(request);

            assertThat(dto.name()).isEqualTo("Name");
            assertThat(dto.email()).isEqualTo("a@b.com");
            assertThat(dto.address()).isEqualTo("Addr");
        }
    }

    @Nested
    @DisplayName("toAssignInput()")
    class ToAssignInputTest {

        @Test
        void maps_userTypeId_from_request_json_to_input_dto() {
            AssignUserTypeRequestJson request = new AssignUserTypeRequestJson(3L);

            AssignUserTypeInputDTO dto = UserJSONMapper.toAssignInput(request);

            assertThat(dto.userTypeId()).isEqualTo(3L);
        }
    }

    @Nested
    @DisplayName("toResponse()")
    class ToResponseTest {

        @Test
        void maps_all_fields_from_output_dto_to_response_json_including_nested_userType() {
            UserOutputDTO output = toOutputDTO(JOAO_CLIENTE);

            UserResponseJson response = UserJSONMapper.toResponse(output);

            assertThat(response.id()).isEqualTo(output.id());
            assertThat(response.name()).isEqualTo(output.name());
            assertThat(response.email()).isEqualTo(output.email());
            assertThat(response.address()).isEqualTo(output.address());
            assertThat(response.userType()).isNotNull();
            assertThat(response.userType().id()).isEqualTo(output.userType().id());
            assertThat(response.createdAt()).isEqualTo(output.createdAt());
            assertThat(response.lastModifiedAt()).isEqualTo(output.lastModifiedAt());
        }
    }
}

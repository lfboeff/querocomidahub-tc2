package br.com.fiap.querocomidahub.user.application.mapper;

import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDTOMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserDTOMapperTest {

    @Nested
    @DisplayName("toOutputDTO()")
    class ToOutputDTOTest {

        @Test
        void maps_all_fields_from_UserBase_to_UserOutputDTO_including_nested_userType() {
            UserOutputDTO output = UserDTOMapper.toOutputDTO(MARIA_DONA);

            assertThat(output.id()).isEqualTo(MARIA_DONA.getId());
            assertThat(output.name()).isEqualTo(MARIA_DONA.getName());
            assertThat(output.email()).isEqualTo(MARIA_DONA.getEmail());
            assertThat(output.address()).isEqualTo(MARIA_DONA.getAddress());
            assertThat(output.userType()).isNotNull();
            assertThat(output.userType().id()).isEqualTo(MARIA_DONA.getUserType().getId());
            assertThat(output.userType().name()).isEqualTo(MARIA_DONA.getUserType().getName());
            assertThat(output.userType().canManageRestaurants()).isEqualTo(MARIA_DONA.getUserType().canManageRestaurants());
            assertThat(output.createdAt()).isEqualTo(MARIA_DONA.getCreatedAt());
            assertThat(output.lastModifiedAt()).isEqualTo(MARIA_DONA.getLastModifiedAt());
        }
    }
}

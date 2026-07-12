package br.com.fiap.querocomidahub.usertype.application.mapper;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.DONO_DE_RESTAURANTE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserTypeDTOMapper")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeDTOMapperTest {

    @Nested
    @DisplayName("toOutputDTO()")
    class ToOutputDTOTest {

        @Test
        void maps_all_fields_from_UserType_to_UserTypeOutputDTO() {
            UserTypeOutputDTO outputDTO = UserTypeDTOMapper.toOutputDTO(DONO_DE_RESTAURANTE);

            assertThat(outputDTO.id()).isEqualTo(DONO_DE_RESTAURANTE.getId());
            assertThat(outputDTO.name()).isEqualTo(DONO_DE_RESTAURANTE.getName());
            assertThat(outputDTO.isSystem()).isEqualTo(DONO_DE_RESTAURANTE.isSystem());
            assertThat(outputDTO.canManageRestaurants()).isEqualTo(DONO_DE_RESTAURANTE.canManageRestaurants());
            assertThat(outputDTO.createdAt()).isEqualTo(DONO_DE_RESTAURANTE.getCreatedAt());
            assertThat(outputDTO.lastModifiedAt()).isEqualTo(DONO_DE_RESTAURANTE.getLastModifiedAt());
        }
    }
}

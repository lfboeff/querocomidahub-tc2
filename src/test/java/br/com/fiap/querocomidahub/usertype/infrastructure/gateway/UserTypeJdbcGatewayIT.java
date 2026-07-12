package br.com.fiap.querocomidahub.usertype.infrastructure.gateway;

import br.com.fiap.querocomidahub.shared.infrastructure.gateway.IntegrationTestBase;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.MOTOBOY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserTypeJdbcGateway")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeJdbcGatewayIT extends IntegrationTestBase {

    @Autowired
    private UserTypeJdbcGateway userTypeJdbcGateway;

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_all_user_types_ordered_by_id() {
            List<UserType> result = userTypeJdbcGateway.findAll();

            assertThat(result).hasSize(3);
            assertThat(result).extracting(UserType::getId)
                    .containsSequence(1L, 2L, 3L);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_user_type_when_id_exists() {
            Optional<UserType> result = userTypeJdbcGateway.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getName()).isEqualTo("Dono de Restaurante");
            assertThat(result.get().isSystem()).isTrue();
            assertThat(result.get().canManageRestaurants()).isTrue();
            assertThat(result.get().getCreatedAt()).isNotNull();
            assertThat(result.get().getLastModifiedAt()).isNotNull();
        }

        @Test
        void returns_empty_when_id_does_not_exist() {
            Optional<UserType> result = userTypeJdbcGateway.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("insert()")
    class InsertTest {

        @Test
        void persists_user_type_and_returns_generated_id() {
            String name = "Cozinheiro";
            UserType newUserType = UserType.create(name, false);

            Long generatedId = userTypeJdbcGateway.insert(newUserType);
            Optional<UserType> result = userTypeJdbcGateway.findById(generatedId);

            assertThat(generatedId).isNotNull().isPositive();
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(name);
            assertThat(result.get().canManageRestaurants()).isFalse();
            assertThat(result.get().isSystem()).isFalse();
            assertThat(result.get().getCreatedAt()).isNotNull();
            assertThat(result.get().getLastModifiedAt()).isNotNull();
        }

        @Test
        void persists_user_type_with_canManageRestaurants_true() {
            UserType newUserType = UserType.create("Gerente de Cozinha", true);

            Long generatedId = userTypeJdbcGateway.insert(newUserType);
            Optional<UserType> result = userTypeJdbcGateway.findById(generatedId);

            assertThat(result).isPresent();
            assertThat(result.get().canManageRestaurants()).isTrue();
        }

        @Test
        void throws_DataIntegrityViolationException_when_name_is_duplicated() {
            UserType duplicatedUserType = UserType.create(CLIENTE.getName(), false);

            assertThatThrownBy(() -> userTypeJdbcGateway.insert(duplicatedUserType))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void updates_name_of_existing_user_type() {
            String newName = "Novo Nome";
            UserType beforeUpdate = userTypeJdbcGateway.findById(MOTOBOY.getId()).orElseThrow();
            UserType userTypeToUpdate = beforeUpdate.withUpdatedParams(newName, beforeUpdate.canManageRestaurants());

            userTypeJdbcGateway.update(userTypeToUpdate);

            UserType result = userTypeJdbcGateway.findById(userTypeToUpdate.getId()).orElseThrow();

            assertThat(result.getName()).isEqualTo(newName);
            assertThat(result.canManageRestaurants()).isFalse();
            assertThat(result.isSystem()).isFalse();
            assertThat(result.getLastModifiedAt()).isAfter(beforeUpdate.getLastModifiedAt());
        }

        @Test
        void updates_canManageRestaurants_from_false_to_true() {
            UserType beforeUpdate = userTypeJdbcGateway.findById(MOTOBOY.getId()).orElseThrow();
            UserType userTypeToUpdate = beforeUpdate.withUpdatedParams(beforeUpdate.getName(), true);

            userTypeJdbcGateway.update(userTypeToUpdate);

            UserType result = userTypeJdbcGateway.findById(MOTOBOY.getId()).orElseThrow();

            assertThat(result.canManageRestaurants()).isTrue();
            assertThat(result.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void removes_user_type_from_database() {
            Long id = MOTOBOY.getId();

            userTypeJdbcGateway.delete(id);

            assertThat(userTypeJdbcGateway.findById(id)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByName()")
    class ExistsByNameTest {

        @Test
        void returns_true_when_name_exists() {
            assertThat(userTypeJdbcGateway.existsByName(CLIENTE.getName())).isTrue();
        }

        @Test
        void returns_false_when_name_does_not_exist() {
            assertThat(userTypeJdbcGateway.existsByName("NonExistentType")).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByNameForDifferentId()")
    class ExistsByNameForDifferentIdTest {

        @Test
        void returns_true_when_name_is_used_by_another_id() {
            assertThat(userTypeJdbcGateway.existsByNameForDifferentId(CLIENTE.getName(), MOTOBOY.getId())).isTrue();
        }

        @Test
        void returns_false_when_name_belongs_to_the_same_id() {
            assertThat(userTypeJdbcGateway.existsByNameForDifferentId(CLIENTE.getName(), CLIENTE.getId())).isFalse();
        }

        @Test
        void returns_false_when_name_does_not_exist_at_all() {
            assertThat(userTypeJdbcGateway.existsByNameForDifferentId("NonExistentType", CLIENTE.getId())).isFalse();
        }
    }
}

package br.com.fiap.querocomidahub.user.infrastructure.gateway;

import br.com.fiap.querocomidahub.shared.infrastructure.gateway.IntegrationTestBase;
import br.com.fiap.querocomidahub.user.domain.model.ClientUser;
import br.com.fiap.querocomidahub.user.domain.model.RestaurantOwnerUser;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.user.domain.model.UserFactory;
import br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserJdbcGateway")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserJdbcGatewayIT extends IntegrationTestBase {

    @Autowired
    private UserJdbcGateway userJdbcGateway;

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_all_seeded_users_ordered_by_id() {
            List<UserBase> result = userJdbcGateway.findAll();

            assertThat(result).hasSize(3);
            assertThat(result).extracting(UserBase::getId).containsSequence(1L, 2L, 3L);
        }

        @Test
        void maps_each_user_to_correct_subclass_based_on_user_type() {
            List<UserBase> result = userJdbcGateway.findAll();

            assertThat(result.get(0)).isInstanceOf(ClientUser.class);
            assertThat(result.get(1)).isInstanceOf(RestaurantOwnerUser.class);
            assertThat(result.get(2)).isInstanceOf(RestaurantOwnerUser.class);
        }

        @Test
        void loads_nested_user_type_via_join() {
            UserBase joao = userJdbcGateway.findById(1L).orElseThrow();

            assertThat(joao.getUserType()).isNotNull();
            assertThat(joao.getUserType().getName()).isEqualTo("Cliente");
            assertThat(joao.getUserType().canManageRestaurants()).isFalse();
            assertThat(joao.getUserType().isSystem()).isTrue();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_user_when_id_exists() {
            Optional<UserBase> result = userJdbcGateway.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("joao.silva@email.com");
        }

        @Test
        void returns_empty_when_id_does_not_exist() {
            Optional<UserBase> result = userJdbcGateway.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("insert()")
    class InsertTest {

        @Test
        void persists_client_user_and_returns_generated_id() {
            UserBase newUser = UserFactory.create(
                    "New Client", "new.client@email.com", "Rua Nova", UserTypeTestFixtures.CLIENTE);

            Long generatedId = userJdbcGateway.insert(newUser);
            UserBase result = userJdbcGateway.findById(generatedId).orElseThrow();

            assertThat(generatedId).isNotNull().isPositive();
            assertThat(result).isInstanceOf(ClientUser.class);
            assertThat(result.getEmail()).isEqualTo("new.client@email.com");
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        void persists_restaurant_owner_user_with_correct_subclass_on_reload() {
            UserBase newOwner = UserFactory.create(
                    "New Owner", "new.owner@email.com", "Rua Nova", UserTypeTestFixtures.DONO_DE_RESTAURANTE);

            Long generatedId = userJdbcGateway.insert(newOwner);
            UserBase result = userJdbcGateway.findById(generatedId).orElseThrow();

            assertThat(result).isInstanceOf(RestaurantOwnerUser.class);
        }

        @Test
        void throws_DataIntegrityViolationException_when_email_is_duplicated() {
            UserBase duplicate = UserFactory.create(
                    "Duplicate", "joao.silva@email.com", "Rua Y", UserTypeTestFixtures.CLIENTE);

            assertThatThrownBy(() -> userJdbcGateway.insert(duplicate))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void throws_DataIntegrityViolationException_when_user_type_does_not_exist() {
            UserBase invalidType = UserFactory.create(
                    "Invalid", "invalid@email.com", "Rua Z",
                    br.com.fiap.querocomidahub.usertype.domain.model.UserType.reconstitute(
                            999L, "Ghost", false, false,
                            UserTypeTestFixtures.NOW, UserTypeTestFixtures.NOW));

            assertThatThrownBy(() -> userJdbcGateway.insert(invalidType))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void updates_personal_fields_of_existing_user() {
            UserBase before = userJdbcGateway.findById(1L).orElseThrow();
            UserBase toUpdate = before.withUpdatedParams("Updated Name", "updated@email.com", "New Address");

            userJdbcGateway.update(toUpdate);
            UserBase after = userJdbcGateway.findById(1L).orElseThrow();

            assertThat(after.getName()).isEqualTo("Updated Name");
            assertThat(after.getEmail()).isEqualTo("updated@email.com");
            assertThat(after.getAddress()).isEqualTo("New Address");
            assertThat(after.getLastModifiedAt()).isAfterOrEqualTo(before.getLastModifiedAt());
        }

        @Test
        void changes_subclass_when_user_type_is_replaced() {
            UserBase before = userJdbcGateway.findById(1L).orElseThrow();
            assertThat(before).isInstanceOf(ClientUser.class);

            userJdbcGateway.updateUserType(1L, UserTypeTestFixtures.DONO_DE_RESTAURANTE.getId());
            UserBase after = userJdbcGateway.findById(1L).orElseThrow();

            assertThat(after).isInstanceOf(RestaurantOwnerUser.class);
            assertThat(after.getUserType().getId()).isEqualTo(UserTypeTestFixtures.DONO_DE_RESTAURANTE.getId());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void removes_user_from_database() {
            userJdbcGateway.delete(1L);

            assertThat(userJdbcGateway.findById(1L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmailTest {

        @Test
        void returns_true_when_email_exists() {
            assertThat(userJdbcGateway.existsByEmail("joao.silva@email.com")).isTrue();
        }

        @Test
        void returns_false_when_email_does_not_exist() {
            assertThat(userJdbcGateway.existsByEmail("nobody@nowhere.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByEmailForDifferentId()")
    class ExistsByEmailForDifferentIdTest {

        @Test
        void returns_true_when_email_belongs_to_another_id() {
            assertThat(userJdbcGateway.existsByEmailForDifferentId("joao.silva@email.com", 2L)).isTrue();
        }

        @Test
        void returns_false_when_email_belongs_to_the_same_id() {
            assertThat(userJdbcGateway.existsByEmailForDifferentId("joao.silva@email.com", 1L)).isFalse();
        }

        @Test
        void returns_false_when_email_does_not_exist_at_all() {
            assertThat(userJdbcGateway.existsByEmailForDifferentId("nobody@nowhere.com", 1L)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUserTypeId()")
    class ExistsByUserTypeIdTest {

        @Test
        void returns_true_when_user_type_is_referenced_by_at_least_one_user() {
            assertThat(userJdbcGateway.existsByUserTypeId(1L)).isTrue();
        }

        @Test
        void returns_false_when_user_type_is_not_referenced_by_any_user() {
            assertThat(userJdbcGateway.existsByUserTypeId(3L)).isFalse();
        }
    }

    @Nested
    @DisplayName("existsAsRestaurantOwner()")
    class ExistsAsRestaurantOwnerTest {

        @Test
        void returns_true_when_user_owns_at_least_one_restaurant() {
            assertThat(userJdbcGateway.existsAsRestaurantOwner(2L)).isTrue();
        }

        @Test
        void returns_false_when_user_does_not_own_any_restaurant() {
            assertThat(userJdbcGateway.existsAsRestaurantOwner(1L)).isFalse();
        }
    }
}

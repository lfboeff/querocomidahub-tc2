package br.com.fiap.querocomidahub.usertype.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserType")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeTest {

    @Nested
    @DisplayName("create()")
    class UserTypeCreateTest {

        @Test
        void creates_transient_instance_with_sanitized_name() {
            String name = "  Cliente  ";
            boolean canManageRestaurants = false;

            UserType userType = UserType.create(name, canManageRestaurants);

            assertThat(userType.getId()).isNull();
            assertThat(userType.getName()).isEqualTo("Cliente");
            assertThat(userType.isSystem()).isFalse();
            assertThat(userType.canManageRestaurants()).isFalse();
            assertThat(userType.getCreatedAt()).isNull();
            assertThat(userType.getLastModifiedAt()).isNull();
        }

        @Test
        void sets_canManageRestaurants_correctly() {
            String name = "Parceiro";
            boolean canManageRestaurants = true;

            UserType userType = UserType.create(name, canManageRestaurants);

            assertThat(userType.canManageRestaurants()).isTrue();
        }

        @Test
        void throws_NullPointerException_when_name_is_null() {
            String name = null;
            boolean canManageRestaurants = false;

            assertThatThrownBy(() -> UserType.create(name, canManageRestaurants))
                    .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n", "  \t  "})
        void throws_DomainValidationException_when_name_is_blank(String name) {
            boolean canManageRestaurants = false;

            assertThatThrownBy(() -> UserType.create(name, canManageRestaurants))
                    .isInstanceOf(DomainValidationException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class UserTypeReconstituteTest {

        @Test
        void reconstitutes_with_all_fields() {
            UserType userType = UserType.reconstitute(99L, "Admin", true, true, NOW, NOW);

            assertThat(userType.getId()).isEqualTo(99L);
            assertThat(userType.getName()).isEqualTo("Admin");
            assertThat(userType.isSystem()).isTrue();
            assertThat(userType.canManageRestaurants()).isTrue();
            assertThat(userType.getCreatedAt()).isEqualTo(NOW);
            assertThat(userType.getLastModifiedAt()).isEqualTo(NOW);
        }

        @Test
        void throws_NullPointerException_when_id_is_null() {
            assertThatThrownBy(() -> UserType.reconstitute(null, "Admin", true, true, NOW, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_name_is_null() {
            assertThatThrownBy(() -> UserType.reconstitute(99L, null, true, true, NOW, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_createdAt_is_null() {
            assertThatThrownBy(() -> UserType.reconstitute(99L, "Admin", true, true, null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_lastModifiedAt_is_null() {
            assertThatThrownBy(() -> UserType.reconstitute(99L, "Admin", true, true, NOW, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("ensureModifiable()")
    class UserTypeEnsureModifiableTest {

        @Test
        void does_not_throw_exception_when_type_is_not_system() {
            UserType userType = UserType.reconstitute(99L, "Cozinheiro", false, false, NOW, NOW);

            assertThatNoException().isThrownBy(userType::ensureModifiable);
        }

        @Test
        void throws_UserTypeIsSystemException_when_type_is_system() {
            Long id = 99L;
            UserType systemUserType = UserType.reconstitute(id, "Admin", true, false, NOW, NOW);

            assertThatThrownBy(systemUserType::ensureModifiable)
                    .isInstanceOf(UserTypeIsSystemException.class)
                    .hasMessageContaining(String.valueOf(id));
        }
    }

    @Nested
    @DisplayName("withUpdatedParams()")
    class UserTypeWithUpdatedParamsTest {

        @Test
        void returns_new_immutable_instance_with_updated_fields() {
            UserType originalUserType = UserType.reconstitute(99L, "Nome Original", false, false, NOW, NOW);

            UserType updatedUserType = originalUserType.withUpdatedParams("    Nome Atualizado  ", true);

            assertThat(updatedUserType.getId()).isEqualTo(99L);
            assertThat(updatedUserType.getName()).isEqualTo("Nome Atualizado");
            assertThat(updatedUserType.isSystem()).isFalse();
            assertThat(updatedUserType.canManageRestaurants()).isTrue();
            assertThat(updatedUserType.getCreatedAt()).isEqualTo(NOW);
            assertThat(updatedUserType.getLastModifiedAt()).isEqualTo(NOW);
        }

        @Test
        void throws_NullPointerException_when_new_name_is_null() {
            UserType userType = UserType.reconstitute(99L, "Cliente", false, false, NOW, NOW);

            assertThatThrownBy(() -> userType.withUpdatedParams(null, false))
                    .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n", "  \t  "})
        void throws_DomainValidationException_when_new_name_is_blank(String blank) {
            UserType userType = UserType.reconstitute(99L, "Cliente", false, false, NOW, NOW);

            assertThatThrownBy(() -> userType.withUpdatedParams(blank, false))
                    .isInstanceOf(DomainValidationException.class);
        }
    }


    @Nested
    @DisplayName("equals() - entity identity is defined by id")
    class UserTypeEqualsTest {

        @Test
        void returns_true_when_two_user_types_have_same_id_regardless_of_other_fields() {
            UserType a = UserType.reconstitute(99L, "Admin", true, true, NOW, NOW);
            UserType b = UserType.reconstitute(99L, "Administrador", true, true, NOW, NOW);

            assertThat(a).isEqualTo(b);
        }

        @Test
        void returns_false_when_two_user_types_have_different_ids() {
            UserType a = UserType.reconstitute(55L, "Admin", true, true, NOW, NOW);
            UserType b = UserType.reconstitute(99L, "Administrador", true, true, NOW, NOW);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        void returns_false_when_compared_to_null_or_a_different_type() {
            UserType userType = UserType.reconstitute(99L, "Admin", true, true, NOW, NOW);

            assertThat(userType).isNotEqualTo(null);
            assertThat(userType).isNotEqualTo("not a user type");
        }
    }
}

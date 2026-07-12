package br.com.fiap.querocomidahub.user.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.NOW;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.DONO_DE_RESTAURANTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserBase")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserBaseTest {

    @Nested
    @DisplayName("create() — via UserFactory")
    class CreateTest {

        @Test
        void trims_and_lowercases_email() {
            UserBase user = UserFactory.create("  João  ", "  JOAO@EMAIL.COM  ", "  Rua X  ", CLIENTE);

            assertThat(user.getName()).isEqualTo("João");
            assertThat(user.getEmail()).isEqualTo("joao@email.com");
            assertThat(user.getAddress()).isEqualTo("Rua X");
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t"})
        void throws_DomainValidationException_when_name_is_blank(String blank) {
            assertThatThrownBy(() -> UserFactory.create(blank, "a@b.com", "Rua X", CLIENTE))
                    .isInstanceOf(DomainValidationException.class);
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t"})
        void throws_DomainValidationException_when_email_is_blank(String blank) {
            assertThatThrownBy(() -> UserFactory.create("Name", blank, "Rua X", CLIENTE))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("blank");
        }

        @ParameterizedTest(name = "[{index}] \"{0}\"")
        @ValueSource(strings = {"not-an-email", "@no-local.com", "no-at.com", "no-domain@"})
        void throws_DomainValidationException_when_email_is_invalid(String invalid) {
            assertThatThrownBy(() -> UserFactory.create("Name", invalid, "Rua X", CLIENTE))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("valid email");
        }

        @Test
        void throws_DomainValidationException_when_address_is_blank() {
            assertThatThrownBy(() -> UserFactory.create("Name", "a@b.com", "   ", CLIENTE))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        void throws_NullPointerException_when_userType_is_null() {
            assertThatThrownBy(() -> UserFactory.create("Name", "a@b.com", "Rua X", null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute() — via UserFactory")
    class ReconstituteTest {

        @Test
        void reconstitutes_all_fields_without_re_sanitizing() {
            UserBase user = UserFactory.reconstitute(1L, "João  ", "Joao@Email.com", "Rua X", CLIENTE, NOW, NOW);

            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getName()).isEqualTo("João  ");
            assertThat(user.getEmail()).isEqualTo("Joao@Email.com");
            assertThat(user.getAddress()).isEqualTo("Rua X");
            assertThat(user.getCreatedAt()).isEqualTo(NOW);
            assertThat(user.getLastModifiedAt()).isEqualTo(NOW);
        }

        @Test
        void throws_NullPointerException_when_id_is_null() {
            assertThatThrownBy(() -> UserFactory.reconstitute(null, "Name", "a@b.com", "Rua X", CLIENTE, NOW, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_createdAt_is_null() {
            assertThatThrownBy(() -> UserFactory.reconstitute(1L, "Name", "a@b.com", "Rua X", CLIENTE, null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_lastModifiedAt_is_null() {
            assertThatThrownBy(() -> UserFactory.reconstitute(1L, "Name", "a@b.com", "Rua X", CLIENTE, NOW, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("withUpdatedParams()")
    class WithUpdatedParamsTest {

        @Test
        void returns_new_instance_of_the_same_subclass_when_ClientUser() {
            UserBase updated = JOAO_CLIENTE.withUpdatedParams("New Name", "new@email.com", "New Address");

            assertThat(updated).isInstanceOf(ClientUser.class);
            assertThat(updated.getId()).isEqualTo(JOAO_CLIENTE.getId());
            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getEmail()).isEqualTo("new@email.com");
            assertThat(updated.getAddress()).isEqualTo("New Address");
            assertThat(updated.getUserType()).isEqualTo(JOAO_CLIENTE.getUserType());
            assertThat(updated.getCreatedAt()).isEqualTo(JOAO_CLIENTE.getCreatedAt());
            assertThat(updated.getLastModifiedAt()).isEqualTo(JOAO_CLIENTE.getLastModifiedAt());
        }

        @Test
        void returns_new_instance_of_the_same_subclass_when_RestaurantOwnerUser() {
            UserBase updated = MARIA_DONA.withUpdatedParams("New Name", "new@email.com", "New Address");

            assertThat(updated).isInstanceOf(RestaurantOwnerUser.class);
            assertThat(updated.getName()).isEqualTo("New Name");
        }

        @Test
        void sanitizes_new_values() {
            UserBase updated = JOAO_CLIENTE.withUpdatedParams("  Trimmed  ", "  UP@EMAIL.COM  ", "  Addr  ");

            assertThat(updated.getName()).isEqualTo("Trimmed");
            assertThat(updated.getEmail()).isEqualTo("up@email.com");
            assertThat(updated.getAddress()).isEqualTo("Addr");
        }

        @Test
        void throws_DomainValidationException_when_new_email_is_invalid() {
            assertThatThrownBy(() -> JOAO_CLIENTE.withUpdatedParams("Name", "not-email", "Rua X"))
                    .isInstanceOf(DomainValidationException.class);
        }
    }

    @Nested
    @DisplayName("canManageRestaurants()")
    class CanManageRestaurantsTest {

        @Test
        void returns_true_for_RestaurantOwnerUser() {
            assertThat(MARIA_DONA.canManageRestaurants()).isTrue();
        }

        @Test
        void returns_false_for_ClientUser() {
            assertThat(JOAO_CLIENTE.canManageRestaurants()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSameUserType()")
    class HasSameUserTypeTest {

        @Test
        void returns_true_when_user_has_the_given_type() {
            assertThat(JOAO_CLIENTE.hasSameUserType(CLIENTE)).isTrue();
        }

        @Test
        void returns_false_when_user_has_a_different_type() {
            assertThat(JOAO_CLIENTE.hasSameUserType(DONO_DE_RESTAURANTE)).isFalse();
        }
    }

    @Nested
    @DisplayName("isBeingDemotedFrom()")
    class IsBeingDemotedFromTest {

        @Test
        void returns_true_when_owner_is_moved_to_a_non_managing_type() {
            assertThat(MARIA_DONA.isBeingDemotedFrom(CLIENTE)).isTrue();
        }

        @Test
        void returns_false_when_owner_is_moved_to_another_managing_type() {
            assertThat(MARIA_DONA.isBeingDemotedFrom(DONO_DE_RESTAURANTE)).isFalse();
        }

        @Test
        void returns_false_when_client_is_moved_to_a_non_managing_type() {
            assertThat(JOAO_CLIENTE.isBeingDemotedFrom(CLIENTE)).isFalse();
        }

        @Test
        void returns_false_when_client_is_promoted_to_a_managing_type() {
            assertThat(JOAO_CLIENTE.isBeingDemotedFrom(DONO_DE_RESTAURANTE)).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() — entity identity is defined by id")
    class EqualsTest {

        @Test
        void returns_true_when_two_users_have_same_id_regardless_of_other_fields_or_subclass() {
            UserBase a = UserFactory.reconstitute(99L, "Foo", "a@b.com", "Addr", CLIENTE, NOW, NOW);
            UserBase b = UserFactory.reconstitute(99L, "Bar", "c@d.com", "Other", DONO_DE_RESTAURANTE, NOW, NOW);

            assertThat(a).isEqualTo(b);
        }

        @Test
        void returns_false_when_two_users_have_different_ids() {
            UserBase a = UserFactory.reconstitute(1L, "Foo", "a@b.com", "Addr", CLIENTE, NOW, NOW);
            UserBase b = UserFactory.reconstitute(2L, "Foo", "a@b.com", "Addr", CLIENTE, NOW, NOW);

            assertThat(a).isNotEqualTo(b);
        }
    }
}

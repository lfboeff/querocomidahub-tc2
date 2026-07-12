package br.com.fiap.querocomidahub.restaurant.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.NOW;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Restaurant")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantTest {

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void creates_transient_instance_with_sanitized_fields() {
            Restaurant r = Restaurant.create("  Pizzaria  ", "  Av. X  ", "  Italiana  ", "  Seg-Sex  ", 2L);

            assertThat(r.getId()).isNull();
            assertThat(r.getName()).isEqualTo("Pizzaria");
            assertThat(r.getAddress()).isEqualTo("Av. X");
            assertThat(r.getKitchenType()).isEqualTo("Italiana");
            assertThat(r.getOpeningHours()).isEqualTo("Seg-Sex");
            assertThat(r.getOwnerId()).isEqualTo(2L);
            assertThat(r.getCreatedAt()).isNull();
            assertThat(r.getLastModifiedAt()).isNull();
        }

        @ParameterizedTest(name = "[{index}] name=\"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        void throws_DomainValidationException_when_name_is_blank(String blank) {
            assertThatThrownBy(() -> Restaurant.create(blank, "Addr", "Italiana", "Seg-Sex", 2L))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'name'");
        }

        @ParameterizedTest(name = "[{index}] address=\"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        void throws_DomainValidationException_when_address_is_blank(String blank) {
            assertThatThrownBy(() -> Restaurant.create("Name", blank, "Italiana", "Seg-Sex", 2L))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'address'");
        }

        @ParameterizedTest(name = "[{index}] kitchenType=\"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        void throws_DomainValidationException_when_kitchenType_is_blank(String blank) {
            assertThatThrownBy(() -> Restaurant.create("Name", "Addr", blank, "Seg-Sex", 2L))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'kitchenType'");
        }

        @ParameterizedTest(name = "[{index}] openingHours=\"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        void throws_DomainValidationException_when_openingHours_is_blank(String blank) {
            assertThatThrownBy(() -> Restaurant.create("Name", "Addr", "Italiana", blank, 2L))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'openingHours'");
        }

        @Test
        void throws_NullPointerException_when_ownerId_is_null() {
            assertThatThrownBy(() -> Restaurant.create("Name", "Addr", "Italiana", "Seg-Sex", null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class ReconstituteTest {

        @Test
        void reconstitutes_all_fields_without_re_sanitizing() {
            Restaurant r = Restaurant.reconstitute(1L, "Pizzaria  ", "Av. X", "Italiana", "Seg-Sex", 2L, NOW, NOW);

            assertThat(r.getId()).isEqualTo(1L);
            assertThat(r.getName()).isEqualTo("Pizzaria  ");
            assertThat(r.getAddress()).isEqualTo("Av. X");
            assertThat(r.getCreatedAt()).isEqualTo(NOW);
            assertThat(r.getLastModifiedAt()).isEqualTo(NOW);
        }

        @Test
        void throws_NullPointerException_when_id_is_null() {
            assertThatThrownBy(() -> Restaurant.reconstitute(null, "Name", "Addr", "Italiana", "Seg-Sex", 2L, NOW, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_createdAt_is_null() {
            assertThatThrownBy(() -> Restaurant.reconstitute(1L, "Name", "Addr", "Italiana", "Seg-Sex", 2L, null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_lastModifiedAt_is_null() {
            assertThatThrownBy(() -> Restaurant.reconstitute(1L, "Name", "Addr", "Italiana", "Seg-Sex", 2L, NOW, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("withUpdatedParams()")
    class WithUpdatedParamsTest {

        @Test
        void returns_new_instance_with_updated_fields_preserving_id_owner_and_timestamps() {
            Restaurant updated = PIZZARIA_BELLA_NAPOLI.withUpdatedParams(
                    "  New Name  ", "  New Addr  ", "  Mexicana  ", "  Ter-Dom  ");

            assertThat(updated.getId()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getId());
            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getAddress()).isEqualTo("New Addr");
            assertThat(updated.getKitchenType()).isEqualTo("Mexicana");
            assertThat(updated.getOpeningHours()).isEqualTo("Ter-Dom");
            assertThat(updated.getOwnerId()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getOwnerId());
            assertThat(updated.getCreatedAt()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getCreatedAt());
            assertThat(updated.getLastModifiedAt()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getLastModifiedAt());
        }

        @Test
        void throws_DomainValidationException_when_new_name_is_blank() {
            assertThatThrownBy(() -> PIZZARIA_BELLA_NAPOLI.withUpdatedParams("   ", "Addr", "Italiana", "Seg-Sex"))
                    .isInstanceOf(DomainValidationException.class);
        }
    }

    @Nested
    @DisplayName("isOwnedBy()")
    class IsOwnedByTest {

        @Test
        void returns_true_when_caller_is_the_owner() {
            assertThat(PIZZARIA_BELLA_NAPOLI.isOwnedBy(PIZZARIA_BELLA_NAPOLI.getOwnerId())).isTrue();
        }

        @Test
        void returns_false_when_caller_is_not_the_owner() {
            assertThat(PIZZARIA_BELLA_NAPOLI.isOwnedBy(PIZZARIA_BELLA_NAPOLI.getOwnerId() + 1)).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() - entity identity is defined by id")
    class EqualsTest {

        @Test
        void returns_true_when_two_restaurants_have_same_id() {
            Restaurant a = Restaurant.reconstitute(99L, "A", "X", "K1", "H1", 1L, NOW, NOW);
            Restaurant b = Restaurant.reconstitute(99L, "B", "Y", "K2", "H2", 2L, NOW, NOW);

            assertThat(a).isEqualTo(b);
        }

        @Test
        void returns_false_when_two_restaurants_have_different_ids() {
            Restaurant a = Restaurant.reconstitute(1L, "A", "X", "K", "H", 1L, NOW, NOW);
            Restaurant b = Restaurant.reconstitute(2L, "A", "X", "K", "H", 1L, NOW, NOW);

            assertThat(a).isNotEqualTo(b);
        }
    }
}

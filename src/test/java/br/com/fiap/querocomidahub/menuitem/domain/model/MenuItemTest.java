package br.com.fiap.querocomidahub.menuitem.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.NOW;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MenuItem")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemTest {

    private static final BigDecimal VALID_PRICE = new BigDecimal("29.90");
    private static final BigDecimal ZERO_PRICE = BigDecimal.ZERO;
    private static final BigDecimal NEGATIVE_PRICE = new BigDecimal("-1.00");

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void creates_transient_instance_with_sanitized_fields() {
            MenuItem item = MenuItem.create(1L, "  Pizza  ", "  Delicious  ", VALID_PRICE, false, "  /img.jpg  ");

            assertThat(item.getId()).isNull();
            assertThat(item.getRestaurantId()).isEqualTo(1L);
            assertThat(item.getName()).isEqualTo("Pizza");
            assertThat(item.getDescription()).isEqualTo("Delicious");
            assertThat(item.getPrice()).isEqualByComparingTo(VALID_PRICE);
            assertThat(item.isDineInOnly()).isFalse();
            assertThat(item.getPhotoPath()).isEqualTo("/img.jpg");
            assertThat(item.getCreatedAt()).isNull();
            assertThat(item.getLastModifiedAt()).isNull();
        }

        @Test
        void normalizes_null_photoPath_to_null() {
            MenuItem item = MenuItem.create(1L, "Pizza", "Delicious", VALID_PRICE, false, null);

            assertThat(item.getPhotoPath()).isNull();
        }

        @Test
        void normalizes_blank_photoPath_to_null() {
            MenuItem item = MenuItem.create(1L, "Pizza", "Delicious", VALID_PRICE, false, "   ");

            assertThat(item.getPhotoPath()).isNull();
        }

        @ParameterizedTest(name = "[{index}] name=\"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        void throws_DomainValidationException_when_name_is_blank(String blank) {
            assertThatThrownBy(() -> MenuItem.create(1L, blank, "d", VALID_PRICE, false, null))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'name'");
        }

        @ParameterizedTest(name = "[{index}] description=\"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        void throws_DomainValidationException_when_description_is_blank(String blank) {
            assertThatThrownBy(() -> MenuItem.create(1L, "n", blank, VALID_PRICE, false, null))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'description'");
        }

        @ParameterizedTest(name = "[{index}] price={0}")
        @MethodSource("provideInvalidPrices")
        void throws_DomainValidationException_when_price_is_invalid(BigDecimal invalidPrice) {
            assertThatThrownBy(() -> MenuItem.create(1L, "n", "d", invalidPrice, false, null))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("'price'");
        }

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(ZERO_PRICE, NEGATIVE_PRICE);
        }

        @Test
        void throws_NullPointerException_when_restaurantId_is_null() {
            assertThatThrownBy(() -> MenuItem.create(null, "n", "d", VALID_PRICE, false, null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_price_is_null() {
            assertThatThrownBy(() -> MenuItem.create(1L, "n", "d", null, false, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class ReconstituteTest {

        @Test
        void reconstitutes_all_fields_without_re_sanitizing() {
            MenuItem item = MenuItem.reconstitute(1L, 1L, "Pizza  ", "Desc  ", VALID_PRICE, false, "/img.jpg", NOW, NOW);

            assertThat(item.getName()).isEqualTo("Pizza  ");
            assertThat(item.getDescription()).isEqualTo("Desc  ");
            assertThat(item.getCreatedAt()).isEqualTo(NOW);
            assertThat(item.getLastModifiedAt()).isEqualTo(NOW);
        }

        @Test
        void allows_null_photoPath_on_reconstitute() {
            MenuItem item = MenuItem.reconstitute(1L, 1L, "n", "d", VALID_PRICE, false, null, NOW, NOW);

            assertThat(item.getPhotoPath()).isNull();
        }

        @Test
        void throws_NullPointerException_when_id_is_null() {
            assertThatThrownBy(() -> MenuItem.reconstitute(null, 1L, "n", "d", VALID_PRICE, false, null, NOW, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_createdAt_is_null() {
            assertThatThrownBy(() -> MenuItem.reconstitute(1L, 1L, "n", "d", VALID_PRICE, false, null, null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void throws_NullPointerException_when_lastModifiedAt_is_null() {
            assertThatThrownBy(() -> MenuItem.reconstitute(1L, 1L, "n", "d", VALID_PRICE, false, null, NOW, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("withUpdatedParams()")
    class WithUpdatedParamsTest {

        @Test
        void returns_new_instance_preserving_id_restaurantId_and_timestamps() {
            MenuItem updated = PIZZA_MARGHERITA.withUpdatedParams(
                    "  New Name  ", "  New Desc  ", new BigDecimal("50.00"), true, null);

            assertThat(updated.getId()).isEqualTo(PIZZA_MARGHERITA.getId());
            assertThat(updated.getRestaurantId()).isEqualTo(PIZZA_MARGHERITA.getRestaurantId());
            assertThat(updated.getName()).isEqualTo("New Name");
            assertThat(updated.getDescription()).isEqualTo("New Desc");
            assertThat(updated.getPrice()).isEqualByComparingTo("50.00");
            assertThat(updated.isDineInOnly()).isTrue();
            assertThat(updated.getPhotoPath()).isNull();
            assertThat(updated.getCreatedAt()).isEqualTo(PIZZA_MARGHERITA.getCreatedAt());
        }

        @Test
        void throws_DomainValidationException_when_new_price_is_zero() {
            assertThatThrownBy(() ->
                    PIZZA_MARGHERITA.withUpdatedParams("n", "d", BigDecimal.ZERO, false, null))
                    .isInstanceOf(DomainValidationException.class);
        }
    }

    @Nested
    @DisplayName("belongsTo()")
    class BelongsToTest {

        @Test
        void returns_true_when_menu_item_belongs_to_the_given_restaurant() {
            assertThat(PIZZA_MARGHERITA.belongsTo(PIZZA_MARGHERITA.getRestaurantId())).isTrue();
        }

        @Test
        void returns_false_when_menu_item_belongs_to_a_different_restaurant() {
            assertThat(PIZZA_MARGHERITA.belongsTo(PIZZA_MARGHERITA.getRestaurantId() + 1)).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() - entity identity is defined by id")
    class EqualsTest {

        @Test
        void returns_true_when_two_items_have_same_id() {
            MenuItem a = MenuItem.reconstitute(99L, 1L, "A", "d", VALID_PRICE, false, null, NOW, NOW);
            MenuItem b = MenuItem.reconstitute(99L, 2L, "B", "e", new BigDecimal("10.00"), true, "/x", NOW, NOW);

            assertThat(a).isEqualTo(b);
        }

        @Test
        void returns_false_when_two_items_have_different_ids() {
            MenuItem a = MenuItem.reconstitute(1L, 1L, "A", "d", VALID_PRICE, false, null, NOW, NOW);
            MenuItem b = MenuItem.reconstitute(2L, 1L, "A", "d", VALID_PRICE, false, null, NOW, NOW);

            assertThat(a).isNotEqualTo(b);
        }
    }
}

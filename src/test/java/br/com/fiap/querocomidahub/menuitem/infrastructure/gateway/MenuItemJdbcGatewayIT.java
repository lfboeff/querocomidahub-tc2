package br.com.fiap.querocomidahub.menuitem.infrastructure.gateway;

import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MenuItemJdbcGateway")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemJdbcGatewayIT extends IntegrationTestBase {

    @Autowired
    private MenuItemJdbcGateway menuItemJdbcGateway;

    @Nested
    @DisplayName("findAllByRestaurantId()")
    class FindAllByRestaurantIdTest {

        @Test
        void returns_all_seeded_items_of_the_restaurant_ordered_by_id() {
            List<MenuItem> result = menuItemJdbcGateway.findAllByRestaurantId(1L);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(MenuItem::getId).containsSequence(1L, 2L);
        }

        @Test
        void returns_items_belonging_only_to_the_given_restaurant() {
            List<MenuItem> r1 = menuItemJdbcGateway.findAllByRestaurantId(1L);
            List<MenuItem> r2 = menuItemJdbcGateway.findAllByRestaurantId(2L);

            assertThat(r1).allMatch(i -> i.getRestaurantId().equals(1L));
            assertThat(r2).allMatch(i -> i.getRestaurantId().equals(2L));
        }

        @Test
        void returns_empty_list_when_restaurant_has_no_menu_items() {
            List<MenuItem> result = menuItemJdbcGateway.findAllByRestaurantId(9999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_menu_item_when_id_exists() {
            Optional<MenuItem> result = menuItemJdbcGateway.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Pizza Margherita");
            assertThat(result.get().getRestaurantId()).isEqualTo(1L);
            assertThat(result.get().getPhotoPath()).isEqualTo("/images/pizza-margherita.jpg");
        }

        @Test
        void returns_menu_item_with_null_photo_path_when_persisted_as_null() {
            Optional<MenuItem> result = menuItemJdbcGateway.findById(2L);

            assertThat(result).isPresent();
            assertThat(result.get().getPhotoPath()).isNull();
            assertThat(result.get().isDineInOnly()).isTrue();
        }

        @Test
        void returns_empty_when_id_does_not_exist() {
            Optional<MenuItem> result = menuItemJdbcGateway.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("insert()")
    class InsertTest {

        @Test
        void persists_menu_item_and_returns_generated_id() {
            MenuItem newItem = MenuItem.create(1L, "New Pizza", "New description",
                    new BigDecimal("55.00"), false, "/img/new.jpg");

            Long generatedId = menuItemJdbcGateway.insert(newItem);
            Optional<MenuItem> result = menuItemJdbcGateway.findById(generatedId);

            assertThat(generatedId).isNotNull().isPositive();
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("New Pizza");
            assertThat(result.get().getCreatedAt()).isNotNull();
        }

        @Test
        void throws_DataIntegrityViolationException_when_restaurant_does_not_exist() {
            MenuItem invalid = MenuItem.create(9999L, "n", "d", new BigDecimal("10.00"), false, null);

            assertThatThrownBy(() -> menuItemJdbcGateway.insert(invalid))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void updates_fields_of_existing_menu_item() {
            MenuItem before = menuItemJdbcGateway.findById(1L).orElseThrow();
            MenuItem updated = before.withUpdatedParams(
                    "Margherita Especial",
                    "Nova descrição",
                    new BigDecimal("35.00"),
                    true,
                    null);

            menuItemJdbcGateway.update(updated);
            MenuItem after = menuItemJdbcGateway.findById(1L).orElseThrow();

            assertThat(after.getName()).isEqualTo("Margherita Especial");
            assertThat(after.getDescription()).isEqualTo("Nova descrição");
            assertThat(after.getPrice()).isEqualByComparingTo("35.00");
            assertThat(after.isDineInOnly()).isTrue();
            assertThat(after.getPhotoPath()).isNull();
            assertThat(after.getLastModifiedAt()).isAfterOrEqualTo(before.getLastModifiedAt());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void removes_menu_item_from_database() {
            Long id = 2L;

            menuItemJdbcGateway.delete(id);

            assertThat(menuItemJdbcGateway.findById(id)).isEmpty();
        }
    }
}

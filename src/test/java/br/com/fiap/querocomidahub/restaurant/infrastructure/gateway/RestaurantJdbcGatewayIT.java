package br.com.fiap.querocomidahub.restaurant.infrastructure.gateway;

import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RestaurantJdbcGateway")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantJdbcGatewayIT extends IntegrationTestBase {

    @Autowired
    private RestaurantJdbcGateway restaurantJdbcGateway;

    @Autowired
    private JdbcClient jdbcClient;

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_all_seeded_restaurants_ordered_by_id() {
            List<Restaurant> result = restaurantJdbcGateway.findAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Restaurant::getId).containsSequence(1L, 2L);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_restaurant_when_id_exists() {
            Optional<Restaurant> result = restaurantJdbcGateway.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Pizzaria Bella Napoli");
            assertThat(result.get().getKitchenType()).isEqualTo("Italiana");
            assertThat(result.get().getOwnerId()).isEqualTo(2L);
        }

        @Test
        void returns_empty_when_id_does_not_exist() {
            Optional<Restaurant> result = restaurantJdbcGateway.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("insert()")
    class InsertTest {

        @Test
        void persists_restaurant_and_returns_generated_id() {
            Restaurant newRestaurant = Restaurant.create(
                    "Sushi Bar Yokohama", "Rua Sushi, 1", "Japonesa", "Ter-Dom 18h-23h", 2L);

            Long generatedId = restaurantJdbcGateway.insert(newRestaurant);
            Optional<Restaurant> result = restaurantJdbcGateway.findById(generatedId);

            assertThat(generatedId).isNotNull().isPositive();
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Sushi Bar Yokohama");
            assertThat(result.get().getKitchenType()).isEqualTo("Japonesa");
            assertThat(result.get().getCreatedAt()).isNotNull();
        }

        @Test
        void throws_DataIntegrityViolationException_when_owner_does_not_exist() {
            Restaurant invalid = Restaurant.create("X", "Y", "Z", "W", 9999L);

            assertThatThrownBy(() -> restaurantJdbcGateway.insert(invalid))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void updates_fields_of_existing_restaurant() {
            Restaurant before = restaurantJdbcGateway.findById(1L).orElseThrow();
            Restaurant updated = before.withUpdatedParams(
                    "Bella Napoli Renovada",
                    "Nova Rua, 1",
                    "Italiana Contemporânea",
                    "Todos os dias 12h-00h");

            restaurantJdbcGateway.update(updated);
            Restaurant after = restaurantJdbcGateway.findById(1L).orElseThrow();

            assertThat(after.getName()).isEqualTo("Bella Napoli Renovada");
            assertThat(after.getAddress()).isEqualTo("Nova Rua, 1");
            assertThat(after.getKitchenType()).isEqualTo("Italiana Contemporânea");
            assertThat(after.getOpeningHours()).isEqualTo("Todos os dias 12h-00h");
            assertThat(after.getOwnerId()).isEqualTo(before.getOwnerId());
            assertThat(after.getLastModifiedAt()).isAfterOrEqualTo(before.getLastModifiedAt());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void removes_restaurant_from_database() {
            Long id = 2L;

            restaurantJdbcGateway.delete(id);

            assertThat(restaurantJdbcGateway.findById(id)).isEmpty();
        }

        @Test
        void cascades_to_menu_items_when_restaurant_is_deleted() {
            Long id = 1L;
            Long menuItemsBefore = jdbcClient
                    .sql("SELECT COUNT(*) FROM menu_items WHERE restaurant_id = :id")
                    .param("id", id)
                    .query(Long.class)
                    .single();
            assertThat(menuItemsBefore).isPositive();

            restaurantJdbcGateway.delete(id);

            Long menuItemsAfter = jdbcClient
                    .sql("SELECT COUNT(*) FROM menu_items WHERE restaurant_id = :id")
                    .param("id", id)
                    .query(Long.class)
                    .single();
            assertThat(menuItemsAfter).isZero();
        }
    }
}

package br.com.fiap.querocomidahub.restaurant.infrastructure.gateway;

import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantJdbcGateway implements IRestaurantGateway {

    private final JdbcClient jdbcClient;

    public RestaurantJdbcGateway(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Restaurant.reconstitute(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("kitchen_type"),
                rs.getString("opening_hours"),
                rs.getLong("owner_id"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("last_modified_at", LocalDateTime.class)
        );
    }

    @Override
    public List<Restaurant> findAll() {
        return jdbcClient.sql("""
                        SELECT id, name, address, kitchen_type, opening_hours, owner_id, created_at, last_modified_at
                          FROM restaurants
                         ORDER BY id ASC
                        """)
                .query(RestaurantJdbcGateway::mapRow)
                .list();
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT id, name, address, kitchen_type, opening_hours, owner_id, created_at, last_modified_at
                          FROM restaurants
                         WHERE id = :id
                        """)
                .param("id", id)
                .query(RestaurantJdbcGateway::mapRow)
                .optional();
    }

    @Override
    public Long insert(Restaurant restaurant) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        INSERT INTO restaurants (name, address, kitchen_type, opening_hours, owner_id)
                        VALUES (:name, :address, :kitchenType, :openingHours, :ownerId)
                        """)
                .param("name", restaurant.getName())
                .param("address", restaurant.getAddress())
                .param("kitchenType", restaurant.getKitchenType())
                .param("openingHours", restaurant.getOpeningHours())
                .param("ownerId", restaurant.getOwnerId())
                .update(keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new DataRetrievalFailureException("Failed to retrieve generated key after saving restaurant");
        }
        return generatedId.longValue();
    }

    @Override
    public void update(Restaurant restaurant) {
        jdbcClient.sql("""
                        UPDATE restaurants
                           SET name = :name,
                               address = :address,
                               kitchen_type = :kitchenType,
                               opening_hours = :openingHours,
                               last_modified_at = CURRENT_TIMESTAMP
                         WHERE id = :id
                        """)
                .param("name", restaurant.getName())
                .param("address", restaurant.getAddress())
                .param("kitchenType", restaurant.getKitchenType())
                .param("openingHours", restaurant.getOpeningHours())
                .param("id", restaurant.getId())
                .update();
    }

    @Override
    public void delete(Long id) {
        jdbcClient.sql("DELETE FROM restaurants WHERE id = :id")
                .param("id", id)
                .update();
    }
}

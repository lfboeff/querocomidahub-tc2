package br.com.fiap.querocomidahub.menuitem.infrastructure.gateway;

import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;
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
public class MenuItemJdbcGateway implements IMenuItemGateway {

    private final JdbcClient jdbcClient;

    public MenuItemJdbcGateway(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MenuItem.reconstitute(
                rs.getLong("id"),
                rs.getLong("restaurant_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getBoolean("dine_in_only"),
                rs.getString("photo_path"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("last_modified_at", LocalDateTime.class)
        );
    }

    @Override
    public List<MenuItem> findAllByRestaurantId(Long restaurantId) {
        return jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, dine_in_only, photo_path,
                               created_at, last_modified_at
                          FROM menu_items
                         WHERE restaurant_id = :restaurantId
                         ORDER BY id ASC
                        """)
                .param("restaurantId", restaurantId)
                .query(MenuItemJdbcGateway::mapRow)
                .list();
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, dine_in_only, photo_path,
                               created_at, last_modified_at
                          FROM menu_items
                         WHERE id = :id
                        """)
                .param("id", id)
                .query(MenuItemJdbcGateway::mapRow)
                .optional();
    }

    @Override
    public Long insert(MenuItem menuItem) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        INSERT INTO menu_items (restaurant_id, name, description, price, dine_in_only, photo_path)
                        VALUES (:restaurantId, :name, :description, :price, :dineInOnly, :photoPath)
                        """)
                .param("restaurantId", menuItem.getRestaurantId())
                .param("name", menuItem.getName())
                .param("description", menuItem.getDescription())
                .param("price", menuItem.getPrice())
                .param("dineInOnly", menuItem.isDineInOnly())
                .param("photoPath", menuItem.getPhotoPath())
                .update(keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new DataRetrievalFailureException("Failed to retrieve generated key after saving menu item");
        }
        return generatedId.longValue();
    }

    @Override
    public void update(MenuItem menuItem) {
        jdbcClient.sql("""
                        UPDATE menu_items
                           SET name = :name,
                               description = :description,
                               price = :price,
                               dine_in_only = :dineInOnly,
                               photo_path = :photoPath,
                               last_modified_at = CURRENT_TIMESTAMP
                         WHERE id = :id
                        """)
                .param("name", menuItem.getName())
                .param("description", menuItem.getDescription())
                .param("price", menuItem.getPrice())
                .param("dineInOnly", menuItem.isDineInOnly())
                .param("photoPath", menuItem.getPhotoPath())
                .param("id", menuItem.getId())
                .update();
    }

    @Override
    public void delete(Long id) {
        jdbcClient.sql("""
                        DELETE FROM menu_items
                         WHERE id = :id
                        """)
                .param("id", id)
                .update();
    }
}

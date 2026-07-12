package br.com.fiap.querocomidahub.usertype.infrastructure.gateway;

import br.com.fiap.querocomidahub.usertype.domain.gateway.IUserTypeGateway;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;
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
public class UserTypeJdbcGateway implements IUserTypeGateway {

    private final JdbcClient jdbcClient;

    public UserTypeJdbcGateway(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static UserType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserType.reconstitute(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBoolean("is_system"),
                rs.getBoolean("can_manage_restaurants"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("last_modified_at", LocalDateTime.class)
        );
    }

    @Override
    public List<UserType> findAll() {
        return jdbcClient.sql("""
                        SELECT id, name, is_system, can_manage_restaurants, created_at, last_modified_at
                          FROM user_types
                         ORDER BY id ASC
                        """)
                .query(UserTypeJdbcGateway::mapRow)
                .list();
    }

    @Override
    public Optional<UserType> findById(Long id) {
        return jdbcClient.sql("""
                        SELECT id, name, is_system, can_manage_restaurants, created_at, last_modified_at
                          FROM user_types
                         WHERE id = :id
                        """)
                .param("id", id)
                .query(UserTypeJdbcGateway::mapRow)
                .optional();
    }

    @Override
    public Long insert(UserType userType) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        INSERT INTO user_types (name, can_manage_restaurants)
                        VALUES (:name, :can_manage_restaurants)
                        """)
                .param("name", userType.getName())
                .param("can_manage_restaurants", userType.canManageRestaurants())
                .update(keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new DataRetrievalFailureException("Failed to retrieve generated key after saving user type");
        }
        return generatedId.longValue();
    }

    @Override
    public void update(UserType userType) {
        jdbcClient.sql("""
                        UPDATE user_types
                           SET name = :name,
                               can_manage_restaurants = :canManageRestaurants,
                               last_modified_at = CURRENT_TIMESTAMP
                         WHERE id = :id
                        """)
                .param("name", userType.getName())
                .param("canManageRestaurants", userType.canManageRestaurants())
                .param("id", userType.getId())
                .update();
    }

    @Override
    public void delete(Long id) {
        jdbcClient.sql("""
                        DELETE FROM user_types
                         WHERE id = :id
                        """)
                .param("id", id)
                .update();
    }

    @Override
    public boolean existsByName(String name) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                          FROM user_types
                         WHERE name = :name
                        """)
                .param("name", name)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public boolean existsByNameForDifferentId(String name, Long id) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                          FROM user_types
                         WHERE name = :name
                           AND id <> :id
                        """)
                .param("name", name)
                .param("id", id)
                .query(Long.class)
                .single() > 0;
    }
}

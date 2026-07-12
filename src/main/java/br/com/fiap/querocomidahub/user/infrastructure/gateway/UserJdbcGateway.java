package br.com.fiap.querocomidahub.user.infrastructure.gateway;

import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.user.domain.model.UserFactory;
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
public class UserJdbcGateway implements IUserGateway {

    private final JdbcClient jdbcClient;

    public UserJdbcGateway(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static final String SELECT_USER_WITH_USER_TYPE = """
            SELECT u.id                       AS u_id,
                   u.name                     AS u_name,
                   u.email                    AS u_email,
                   u.address                  AS u_address,
                   u.created_at               AS u_created_at,
                   u.last_modified_at         AS u_last_modified_at,
                   ut.id                      AS ut_id,
                   ut.name                    AS ut_name,
                   ut.is_system               AS ut_is_system,
                   ut.can_manage_restaurants  AS ut_can_manage_restaurants,
                   ut.created_at              AS ut_created_at,
                   ut.last_modified_at        AS ut_last_modified_at
              FROM users u
              JOIN user_types ut ON ut.id = u.user_type_id
            """;

    private static UserBase mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserType userType = UserType.reconstitute(
                rs.getLong("ut_id"),
                rs.getString("ut_name"),
                rs.getBoolean("ut_is_system"),
                rs.getBoolean("ut_can_manage_restaurants"),
                rs.getObject("ut_created_at", LocalDateTime.class),
                rs.getObject("ut_last_modified_at", LocalDateTime.class)
        );

        return UserFactory.reconstitute(
                rs.getLong("u_id"),
                rs.getString("u_name"),
                rs.getString("u_email"),
                rs.getString("u_address"),
                userType,
                rs.getObject("u_created_at", LocalDateTime.class),
                rs.getObject("u_last_modified_at", LocalDateTime.class)
        );
    }

    @Override
    public List<UserBase> findAll() {
        return jdbcClient.sql(SELECT_USER_WITH_USER_TYPE + " ORDER BY u.id ASC")
                .query(UserJdbcGateway::mapRow)
                .list();
    }

    @Override
    public Optional<UserBase> findById(Long id) {
        return jdbcClient.sql(SELECT_USER_WITH_USER_TYPE + " WHERE u.id = :id")
                .param("id", id)
                .query(UserJdbcGateway::mapRow)
                .optional();
    }

    @Override
    public Long insert(UserBase user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        INSERT INTO users (name, email, address, user_type_id)
                        VALUES (:name, :email, :address, :userTypeId)
                        """)
                .param("name", user.getName())
                .param("email", user.getEmail())
                .param("address", user.getAddress())
                .param("userTypeId", user.getUserType().getId())
                .update(keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new DataRetrievalFailureException("Failed to retrieve generated key after saving user");
        }
        return generatedId.longValue();
    }

    @Override
    public void update(UserBase user) {
        jdbcClient.sql("""
                        UPDATE users
                           SET name = :name,
                               email = :email,
                               address = :address,
                               user_type_id = :userTypeId,
                               last_modified_at = CURRENT_TIMESTAMP
                         WHERE id = :id
                        """)
                .param("name", user.getName())
                .param("email", user.getEmail())
                .param("address", user.getAddress())
                .param("userTypeId", user.getUserType().getId())
                .param("id", user.getId())
                .update();
    }

    @Override
    public void updateUserType(Long userId, Long userTypeId) {
        jdbcClient.sql("""
                        UPDATE users
                           SET user_type_id = :userTypeId,
                               last_modified_at = CURRENT_TIMESTAMP
                         WHERE id = :id
                        """)
                .param("userTypeId", userTypeId)
                .param("id", userId)
                .update();
    }

    @Override
    public void delete(Long id) {
        jdbcClient.sql("""
                        DELETE FROM users
                         WHERE id = :id
                        """)
                .param("id", id)
                .update();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                          FROM users
                         WHERE email = :email
                        """)
                .param("email", email)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public boolean existsByEmailForDifferentId(String email, Long id) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                          FROM users
                         WHERE email = :email
                           AND id <> :id
                        """)
                .param("email", email)
                .param("id", id)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public boolean existsByUserTypeId(Long userTypeId) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                         FROM users
                        WHERE user_type_id = :userTypeId
                        """)
                .param("userTypeId", userTypeId)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public boolean existsAsRestaurantOwner(Long userId) {
        return jdbcClient.sql("""
                        SELECT COUNT(*)
                          FROM restaurants
                         WHERE owner_id = :userId
                        """)
                .param("userId", userId)
                .query(Long.class)
                .single() > 0;
    }
}

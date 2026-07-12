package br.com.fiap.querocomidahub.user.domain.gateway;

import br.com.fiap.querocomidahub.user.domain.model.UserBase;

import java.util.List;
import java.util.Optional;

public interface IUserGateway {

    List<UserBase> findAll();

    Optional<UserBase> findById(Long id);

    Long insert(UserBase user);

    void update(UserBase user);

    void updateUserType(Long userId, Long userTypeId);

    void delete(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailForDifferentId(String email, Long id);

    boolean existsByUserTypeId(Long userTypeId);

    boolean existsAsRestaurantOwner(Long userId);
}

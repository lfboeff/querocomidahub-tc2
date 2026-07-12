package br.com.fiap.querocomidahub.usertype.domain.gateway;

import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.util.List;
import java.util.Optional;

public interface IUserTypeGateway {

    List<UserType> findAll();

    Optional<UserType> findById(Long id);

    Long insert(UserType userType);

    void update(UserType userType);

    void delete(Long id);

    boolean existsByName(String name);

    boolean existsByNameForDifferentId(String name, Long id);
}

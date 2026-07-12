package br.com.fiap.querocomidahub.menuitem.domain.gateway;

import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface IMenuItemGateway {

    List<MenuItem> findAllByRestaurantId(Long restaurantId);

    Optional<MenuItem> findById(Long id);

    Long insert(MenuItem menuItem);

    void update(MenuItem menuItem);

    void delete(Long id);
}

package br.com.fiap.querocomidahub.restaurant.domain.gateway;

import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface IRestaurantGateway {

    List<Restaurant> findAll();

    Optional<Restaurant> findById(Long id);

    Long insert(Restaurant restaurant);

    void update(Restaurant restaurant);

    void delete(Long id);
}

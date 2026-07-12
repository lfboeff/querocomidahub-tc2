package br.com.fiap.querocomidahub.restaurant;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.model.Restaurant;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public final class RestaurantTestFixtures {

    private RestaurantTestFixtures() {
    }

    public static final LocalDateTime NOW = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);

    public static final Restaurant PIZZARIA_BELLA_NAPOLI = Restaurant.reconstitute(
            1L,
            "Pizzaria Bella Napoli",
            "Av. Paulista, 1000 - São Paulo, SP",
            "Italiana",
            "Seg-Sex 11h-22h, Sab-Dom 11h-23h",
            2L,
            NOW,
            NOW
    );

    public static final Restaurant CHURRASCARIA_GAUCHA = Restaurant.reconstitute(
            2L,
            "Churrascaria Gaúcha",
            "Rua Augusta, 200 - São Paulo, SP",
            "Brasileira",
            "Ter-Dom 12h-23h",
            2L,
            NOW,
            NOW
    );

    public static List<Restaurant> restaurantList() {
        return List.of(PIZZARIA_BELLA_NAPOLI, CHURRASCARIA_GAUCHA);
    }

    public static RestaurantOutputDTO toOutputDTO(Restaurant restaurant) {
        return new RestaurantOutputDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getKitchenType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getLastModifiedAt(),
                null
        );
    }

    public static List<RestaurantOutputDTO> outputDTOList() {
        return restaurantList().stream()
                .map(RestaurantTestFixtures::toOutputDTO)
                .toList();
    }
}

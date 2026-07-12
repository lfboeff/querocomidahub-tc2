package br.com.fiap.querocomidahub.restaurant.application.dto;

public record RestaurantInputDTO(
        String name,
        String address,
        String kitchenType,
        String openingHours
) {
}

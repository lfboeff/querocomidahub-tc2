package br.com.fiap.querocomidahub.restaurant.application.dto;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;

import java.time.LocalDateTime;
import java.util.List;

public record RestaurantOutputDTO(
        Long id,
        String name,
        String address,
        String kitchenType,
        String openingHours,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        List<MenuItemOutputDTO> menuItems
) {
}

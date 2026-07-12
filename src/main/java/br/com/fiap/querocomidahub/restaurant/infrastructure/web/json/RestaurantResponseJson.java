package br.com.fiap.querocomidahub.restaurant.infrastructure.web.json;

import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemResponseJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record RestaurantResponseJson(
        @Schema(description = "Restaurant ID", example = "1")
        Long id,

        @Schema(description = "Name of the restaurant", example = "Pizzaria Bella Napoli")
        String name,

        @Schema(description = "Postal address of the restaurant",
                example = "Av. Paulista, 1000 - São Paulo, SP")
        String address,

        @Schema(description = "Kitchen type / cuisine served", example = "Italiana")
        String kitchenType,

        @Schema(description = "Opening hours description",
                example = "Mon-Fri 11:00-22:00, Sat-Sun 11:00-23:00")
        String openingHours,

        @Schema(description = "ID of the user who owns this restaurant", example = "2")
        Long ownerId,

        @Schema(description = "Creation timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime lastModifiedAt,

        @Schema(description = "Menu items embedded (populated only on GET /{id}; omitted on GET list)")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<MenuItemResponseJson> menuItems
) {
}

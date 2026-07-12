package br.com.fiap.querocomidahub.menuitem.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MenuItemResponseJson(
        @Schema(description = "Menu item ID", example = "1")
        Long id,

        @Schema(description = "ID of the restaurant this item belongs to", example = "1")
        Long restaurantId,

        @Schema(description = "Name of the menu item", example = "Pizza Margherita")
        String name,

        @Schema(description = "Detailed description of the menu item",
                example = "Fresh mozzarella, tomato, basil and extra virgin olive oil")
        String description,

        @Schema(description = "Price in the establishment's local currency (BRL)", example = "29.90")
        BigDecimal price,

        @Schema(description = "If true, this item is only available for on-premises consumption",
                example = "false")
        boolean dineInOnly,

        @Schema(description = "Path to a photo of the menu item (may be null)",
                example = "/img/margherita.jpg")
        String photoPath,

        @Schema(description = "Creation timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime lastModifiedAt
) {
}

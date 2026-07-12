package br.com.fiap.querocomidahub.menuitem.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MenuItemOutputDTO(
        Long id,
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoPath,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}

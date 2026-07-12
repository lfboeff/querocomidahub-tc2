package br.com.fiap.querocomidahub.menuitem.application.dto;

import java.math.BigDecimal;

public record MenuItemInputDTO(
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoPath
) {
}

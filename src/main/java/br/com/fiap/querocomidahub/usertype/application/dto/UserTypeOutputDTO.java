package br.com.fiap.querocomidahub.usertype.application.dto;

import java.time.LocalDateTime;

public record UserTypeOutputDTO(
        Long id,
        String name,
        boolean isSystem,
        boolean canManageRestaurants,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}

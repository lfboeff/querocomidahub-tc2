package br.com.fiap.querocomidahub.usertype.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserTypeResponseJson(
        @Schema(description = "User type ID", example = "3")
        Long id,

        @Schema(description = "Name of the user type", example = "Motoboy")
        String name,

        @Schema(description = "Whether this is a system type - cannot be modified or deleted via API",
                example = "false")
        boolean isSystem,

        @Schema(description = "Whether this type can manage restaurants", example = "false")
        boolean canManageRestaurants,

        @Schema(description = "Creation timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime lastModifiedAt
) {
}

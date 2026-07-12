package br.com.fiap.querocomidahub.user.infrastructure.web.json;

import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeResponseJson;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponseJson(
        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "Full name of the user", example = "João da Silva")
        String name,

        @Schema(description = "Email address of the user", example = "joao.silva@email.com")
        String email,

        @Schema(description = "Postal address of the user", example = "Rua das Flores, 123 - São Paulo, SP")
        String address,

        @Schema(description = "User type assigned to this user")
        UserTypeResponseJson userType,

        @Schema(description = "Creation timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Last modification timestamp (UTC)", example = "2025-01-01T00:00:00")
        LocalDateTime lastModifiedAt
) {
}

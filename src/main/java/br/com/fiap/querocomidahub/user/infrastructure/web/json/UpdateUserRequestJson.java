package br.com.fiap.querocomidahub.user.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestJson(
        @Schema(description = "Full name of the user", example = "João da Silva",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String name,

        @Schema(description = "Email address of the user (must be unique)", example = "joao.silva@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email @Size(max = 150)
        String email,

        @Schema(description = "Postal address of the user", example = "Rua das Flores, 123 - São Paulo, SP",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String address
) {
    @Override
    public String toString() {
        return "UpdateUserRequestJson{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

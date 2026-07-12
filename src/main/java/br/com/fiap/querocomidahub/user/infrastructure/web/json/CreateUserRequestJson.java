package br.com.fiap.querocomidahub.user.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateUserRequestJson(
        @Schema(description = "Full name of the user", example = "João da Silva",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String name,

        @Schema(description = "Email address of the user (must be unique)", example = "joao.silva@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email @Size(max = 150)
        String email,

        @Schema(description = "Postal address of the user", example = "Rua das Flores, 123 - São Paulo, SP",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String address,

        @Schema(description = "ID of the user type", example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive
        Long userTypeId
) {
    @Override
    public String toString() {
        return "CreateUserRequestJson{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", userTypeId=" + userTypeId +
                '}';
    }
}

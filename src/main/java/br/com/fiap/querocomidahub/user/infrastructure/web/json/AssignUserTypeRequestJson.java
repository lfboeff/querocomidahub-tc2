package br.com.fiap.querocomidahub.user.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssignUserTypeRequestJson(
        @Schema(description = "ID of the user type to assign to the user", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive
        Long userTypeId
) {
    @Override
    public String toString() {
        return "AssignUserTypeRequestJson{" +
                "userTypeId=" + userTypeId +
                '}';
    }
}

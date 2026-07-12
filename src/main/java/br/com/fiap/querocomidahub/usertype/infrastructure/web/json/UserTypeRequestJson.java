package br.com.fiap.querocomidahub.usertype.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserTypeRequestJson(
        @Schema(description = "Name of the user type", example = "Motoboy",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 50)
        String name,

        @Schema(description = "Whether this type can manage restaurants", example = "false")
        boolean canManageRestaurants
) {
    @Override
    public String toString() {
        return "UserTypeRequestJson{" +
                "name='" + name + '\'' +
                ", canManageRestaurants=" + canManageRestaurants +
                '}';
    }
}

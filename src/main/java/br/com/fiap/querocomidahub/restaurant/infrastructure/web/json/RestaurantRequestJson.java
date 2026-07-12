package br.com.fiap.querocomidahub.restaurant.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantRequestJson(
        @Schema(description = "Name of the restaurant", example = "Pizzaria Bella Napoli",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String name,

        @Schema(description = "Postal address of the restaurant",
                example = "Av. Paulista, 1000 - São Paulo, SP",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 500)
        String address,

        @Schema(description = "Kitchen type / cuisine served", example = "Italiana",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 80)
        String kitchenType,

        @Schema(description = "Opening hours description",
                example = "Mon-Fri 11:00-22:00, Sat-Sun 11:00-23:00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 200)
        String openingHours
) {
    @Override
    public String toString() {
        return "RestaurantRequestJson{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", kitchenType='" + kitchenType + '\'' +
                ", openingHours='" + openingHours + '\'' +
                '}';
    }
}

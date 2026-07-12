package br.com.fiap.querocomidahub.menuitem.infrastructure.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequestJson(
        @Schema(description = "Name of the menu item", example = "Pizza Margherita",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String name,

        @Schema(description = "Detailed description of the menu item",
                example = "Fresh mozzarella, tomato, basil and extra virgin olive oil",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 500)
        String description,

        @Schema(description = "Price in the establishment's local currency (BRL)",
                example = "29.90",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @DecimalMin(value = "0.01")
        BigDecimal price,

        @Schema(description = "If true, this item is only available for on-premises consumption",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        boolean dineInOnly,

        @Schema(description = "Optional path to a photo of the menu item",
                example = "/img/margherita.jpg")
        @Size(max = 500)
        String photoPath
) {
    @Override
    public String toString() {
        return "MenuItemRequestJson{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dineInOnly=" + dineInOnly +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }
}

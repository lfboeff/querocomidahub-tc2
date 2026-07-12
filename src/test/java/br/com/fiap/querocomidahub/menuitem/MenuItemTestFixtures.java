package br.com.fiap.querocomidahub.menuitem;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public final class MenuItemTestFixtures {

    private MenuItemTestFixtures() {
    }

    public static final LocalDateTime NOW = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);

    public static final MenuItem PIZZA_MARGHERITA = MenuItem.reconstitute(
            1L,
            1L,
            "Pizza Margherita",
            "Mussarela de búfala, tomate e manjericão fresco",
            new BigDecimal("42.90"),
            false,
            "/img/margherita.jpg",
            NOW,
            NOW
    );

    public static final MenuItem CALZONE_PRESUNTO = MenuItem.reconstitute(
            2L,
            1L,
            "Calzone de Presunto",
            "Massa fechada recheada com presunto, mussarela e orégano",
            new BigDecimal("38.00"),
            true,
            null,
            NOW,
            NOW
    );

    public static List<MenuItem> menuItemList() {
        return List.of(PIZZA_MARGHERITA, CALZONE_PRESUNTO);
    }

    public static MenuItemOutputDTO toOutputDTO(MenuItem menuItem) {
        return new MenuItemOutputDTO(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getPhotoPath(),
                menuItem.getCreatedAt(),
                menuItem.getLastModifiedAt()
        );
    }

    public static List<MenuItemOutputDTO> outputDTOList() {
        return menuItemList().stream()
                .map(MenuItemTestFixtures::toOutputDTO)
                .toList();
    }
}

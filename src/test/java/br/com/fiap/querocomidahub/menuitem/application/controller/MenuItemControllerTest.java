package br.com.fiap.querocomidahub.menuitem.application.controller;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.application.usecase.CreateMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.DeleteMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.GetMenuItemByIdUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.ListMenuItemsUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.UpdateMenuItemUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.menuItemList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuItemController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemControllerTest {

    @Mock
    private ListMenuItemsUseCase listMenuItemsUseCase;

    @Mock
    private GetMenuItemByIdUseCase getMenuItemByIdUseCase;

    @Mock
    private CreateMenuItemUseCase createMenuItemUseCase;

    @Mock
    private UpdateMenuItemUseCase updateMenuItemUseCase;

    @Mock
    private DeleteMenuItemUseCase deleteMenuItemUseCase;

    private MenuItemController menuItemController;

    private static final MenuItemInputDTO VALID_INPUT = new MenuItemInputDTO(
            "n", "d", new BigDecimal("10.00"), false, null);

    @BeforeEach
    void setUp() {
        menuItemController = MenuItemController.create(
                listMenuItemsUseCase,
                getMenuItemByIdUseCase,
                createMenuItemUseCase,
                updateMenuItemUseCase,
                deleteMenuItemUseCase);
    }

    @Nested
    @DisplayName("findAll(restaurantId)")
    class FindAllTest {

        @Test
        void delegates_to_list_use_case_and_maps_each_domain_to_output_dto() {
            when(listMenuItemsUseCase.run(1L)).thenReturn(menuItemList());

            List<MenuItemOutputDTO> result = menuItemController.findAll(1L);

            assertThat(result).hasSize(menuItemList().size());

            verify(listMenuItemsUseCase).run(1L);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void delegates_to_get_by_id_use_case() {
            when(getMenuItemByIdUseCase.run(1L, PIZZA_MARGHERITA.getId())).thenReturn(PIZZA_MARGHERITA);

            MenuItemOutputDTO result = menuItemController.findById(1L, PIZZA_MARGHERITA.getId());

            assertThat(result.id()).isEqualTo(PIZZA_MARGHERITA.getId());

            verify(getMenuItemByIdUseCase).run(1L, PIZZA_MARGHERITA.getId());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void delegates_to_create_use_case_with_restaurant_id_input_and_caller_id() {
            when(createMenuItemUseCase.run(1L, VALID_INPUT, 2L)).thenReturn(42L);

            Long result = menuItemController.create(1L, VALID_INPUT, 2L);

            assertThat(result).isEqualTo(42L);

            verify(createMenuItemUseCase).run(1L, VALID_INPUT, 2L);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void delegates_to_update_use_case() {
            menuItemController.update(1L, 5L, VALID_INPUT, 2L);

            verify(updateMenuItemUseCase).run(1L, 5L, VALID_INPUT, 2L);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void delegates_to_delete_use_case() {
            menuItemController.delete(1L, 5L, 2L);

            verify(deleteMenuItemUseCase).run(1L, 5L, 2L);
        }
    }
}

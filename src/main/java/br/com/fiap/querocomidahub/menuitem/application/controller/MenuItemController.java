package br.com.fiap.querocomidahub.menuitem.application.controller;

import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.application.mapper.MenuItemDTOMapper;
import br.com.fiap.querocomidahub.menuitem.application.usecase.CreateMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.DeleteMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.GetMenuItemByIdUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.ListMenuItemsUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.UpdateMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.domain.model.MenuItem;

import java.util.List;

public final class MenuItemController {

    private final ListMenuItemsUseCase listMenuItemsUseCase;
    private final GetMenuItemByIdUseCase getMenuItemByIdUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;

    private MenuItemController(ListMenuItemsUseCase listMenuItemsUseCase,
                               GetMenuItemByIdUseCase getMenuItemByIdUseCase,
                               CreateMenuItemUseCase createMenuItemUseCase,
                               UpdateMenuItemUseCase updateMenuItemUseCase,
                               DeleteMenuItemUseCase deleteMenuItemUseCase) {
        this.listMenuItemsUseCase = listMenuItemsUseCase;
        this.getMenuItemByIdUseCase = getMenuItemByIdUseCase;
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
    }

    public static MenuItemController create(ListMenuItemsUseCase listMenuItemsUseCase,
                                            GetMenuItemByIdUseCase getMenuItemByIdUseCase,
                                            CreateMenuItemUseCase createMenuItemUseCase,
                                            UpdateMenuItemUseCase updateMenuItemUseCase,
                                            DeleteMenuItemUseCase deleteMenuItemUseCase) {
        return new MenuItemController(
                listMenuItemsUseCase,
                getMenuItemByIdUseCase,
                createMenuItemUseCase,
                updateMenuItemUseCase,
                deleteMenuItemUseCase
        );
    }

    public List<MenuItemOutputDTO> findAll(Long restaurantId) {
        return listMenuItemsUseCase.run(restaurantId)
                .stream()
                .map(MenuItemDTOMapper::toOutputDTO)
                .toList();
    }

    public MenuItemOutputDTO findById(Long restaurantId, Long id) {
        MenuItem menuItem = getMenuItemByIdUseCase.run(restaurantId, id);
        return MenuItemDTOMapper.toOutputDTO(menuItem);
    }

    public Long create(Long restaurantId, MenuItemInputDTO inputDTO, Long callerId) {
        return createMenuItemUseCase.run(restaurantId, inputDTO, callerId);
    }

    public void update(Long restaurantId, Long id, MenuItemInputDTO inputDTO, Long callerId) {
        updateMenuItemUseCase.run(restaurantId, id, inputDTO, callerId);
    }

    public void delete(Long restaurantId, Long id, Long callerId) {
        deleteMenuItemUseCase.run(restaurantId, id, callerId);
    }
}
